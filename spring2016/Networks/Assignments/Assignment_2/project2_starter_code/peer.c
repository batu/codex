/*
 * peer.c
 *
 * Authors: Ed Bardsley <ebardsle+441@andrew.cmu.edu>,
 *          Dave Andersen
 * Class: 15-441 (Spring 2005)
 *
 * Skeleton for 15-441 Project 2.
 *
 */

 /*
 * Submitted Project 2.
 *
 */

#include <sys/types.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "debug.h"
#include "spiffy.h"
#include "bt_parse.h"
#include "input_buffer.h"
#include "chunk.h"


// User created content.
// #include "connection.h" 
// #include "queue.h"
// #include "packet_header"
#include "helper_functions.h"



#define SS_TRESH 64
#define WINDOW_SIZE 1  //8 // obsolete
#define MAGIC_NUMBER 15441
#define CHUNK_DATA_SIZE (512*1024)
#define MAX_CHUNKS 375 // (512*1024 / 1400);

#define WHOHAS  0
#define IHAVE   1
#define GET     2
#define DATA    3
#define ACK     4
#define DENIED  5

// FLAG TO NOTIFY THAT WHAT MY CONGESTION CONTROL WOULD DO IF A PACKET WAS DROPPED.
#define PACKAGE_DROPPED 0

#define SLOW_START 0
#define C_AVOIDANCE 1
#define F_RETRANSMIT 2

#define PROGRAM_OUTPUT_PATH "/tmp/problem2-peer.txt"


void peer_run(bt_config_t *config);

// DEFINING THE GLOBAL VARIABLES. Couldnt figure out how to safely abstract 
// these to headers
bt_config_t config;
connection_list_t connection_list;
int connection_id_pool = 0;
int seq_num_buffer[MAX_WINDOW_SIZE];

/*
###############################################################################
############################   PACKAGE FUNCTIONS   ############################
###############################################################################
*/

// Takes the package type and creates packet based on the type. It can create  
// WHOHAS, IHAVE or GET.
packet_t* create_package(int packet_type){

    struct packet_t *packet;

    packet = (struct packet_t *)malloc(sizeof(struct packet_t));
    packet->packet_header.magic_num = MAGIC_NUMBER;
    packet->packet_header.version_num = 1;
    packet->packet_header.packet_type = packet_type;
    //endianSwap_Host_to_Network(packet);

    return packet;
}

// creates a data package and assigns the package to the correct connection.
packet_t* create_DATA_package(int seq_num, int connection_id){

    struct packet_t *packet;

    packet = (struct packet_t *)malloc(sizeof(struct packet_t));

    packet->packet_header.magic_num = MAGIC_NUMBER;
    packet->packet_header.version_num = 1;
    packet->packet_header.packet_type = DATA;
    packet->packet_header.seq_num = seq_num;

    packet->connection_id = connection_id;
    gettimeofday(&packet->send_time, NULL);
    //endianSwap_Host_to_Network(packet);

    return packet;
}

// creates a ACK package and assigns the package to the correct connection.
packet_t* create_ACK_package(int ack_num, int connection_id){

    struct packet_t *packet;

    packet = (struct packet_t *)malloc(sizeof(struct packet_t));

    packet->packet_header.magic_num = MAGIC_NUMBER;
    packet->packet_header.version_num = 1;
    packet->packet_header.packet_type = ACK;
    packet->packet_header.ack_num = ack_num;
    packet->connection_id = connection_id;
    //endianSwap_Host_to_Network(packet);

    return packet;
}

// Handler fucntion for cleaner sending.
void send_packet(packet_t *packet, int target_socket){

  int i;
  for (i = 0; i < BT_MAX_PEERS; i++){
    // problem with the correct identity and address.
    if (config.peers[i].id ){  // != config.identity){
        spiffy_sendto(target_socket, packet, sizeof(*packet), 0, (struct sockaddr *) &config.peers[i].addr, sizeof(config.peers[i].addr));
    }
  }  
}

/*
###############################################################################
#########################   TCP CONNECTION FUNCTIONS   ########################
###############################################################################
*/

connection_t* add_connection(){
  
  // Malloc the conneciton.
  connection_t *connection = (struct connection_t *) malloc (sizeof(struct connection_t));

  // Initilize the connection Queue that will hold the packets to be sent within the connection.
  connection->data_queue = createQueue();
  // Connection id is a unique identifier. Currently increasing int, can be done smarter.
  connection->id = connection_id_pool;

  // set the mode to Slow Start and the slow start treshold to 64.
  connection->mode = SLOW_START;
  connection->SS_treshold = SS_TRESH;

  // Set the values used in TCP sliding window.
  connection->window_size = WINDOW_SIZE;
  connection->last_packet_acked = 0;
  connection->last_packet_sent = 0;
  connection->last_packet_available = WINDOW_SIZE;

  // Additional offset is the value used in congestion
  connection->addition_offset = 0;

  connection->same_ack_counter = 0;
  connection->last_ack = -1;

  connection->package_dropped = 0;
  // keeping track of the start time.
  gettimeofday(&connection->start_time, NULL);


  // if connection list empty initilize list with the new connection, if not add it to
  // the list of connections.
  if (connection_list.connections == NULL){
    connection_list.connections = connection;
    connection_id_pool++;
    return connection;
  }

  connection_t *active_connection;
  active_connection = connection_list.connections;

  while(active_connection->next != NULL){ 
    active_connection = active_connection->next;
  }

  active_connection->next = connection;
  connection_id_pool++;
  return connection;
}

// Return a connection from the global connection list based on the ID given.
connection_t* retrieve_conection(int connection_id){
  if (connection_list.connections == NULL){
      printf("No active connectioins found.\n");
      return NULL;
  }
  if (connection_list.connections->id == connection_id){
    return connection_list.connections;
  }
  
  connection_t *active_connection = connection_list.connections;

  while(active_connection != NULL){ 
    if (active_connection->id == connection_id){
      return active_connection;
    }
    active_connection = active_connection->next;
  }

  printf("Connection with given ID not found.\n");
  return NULL;
}


// Initilize the sliding window. Rest will be controlled by the DATA/ACK packets.
void start_sliding_window(int connection_id){
  
  // get the queue, pop and send when you have space
  connection_t *connection = retrieve_conection(connection_id);
  packet_t *packet;
  connection->last_packet_available = connection->last_packet_acked + connection->window_size;
  packet = deQueue(connection->data_queue);
  if (packet == NULL){
    return;
  }

  add_packet2buffer(packet, connection);
  send_packet(packet, config.sock);
  connection->last_packet_sent++; 
}


/*
###############################################################################
###########################   Congestion Controll   ###########################
###############################################################################
*/

void congestion_controll(connection_t *connection){

// START OF THE CONGESTION CONTROL IMPLEMENTATION
// PLEASE NOTE. Can not drop packages as my TCP implementation doesnt support it yet. 
// I will sudo code the if statement until my TCP is fully implemented to run in case of a package drop.
// Thank you your understanding.
// PACKAGE_DROPPED == 0;
  // int package_dropped = PACKAGE_DROPPED;
int package_dropped = connection->package_dropped;


  // This block of code is used to test the congestion control by artifically acting as if the connection
  // dropped a packet once in a while (~.5%)
  /*
    float drop_chance = rand();
    double max = 11474836;
    printf("Printing random chance: |%f|\n", drop_chance);
    if (drop_chance < max){
      package_dropped = 1;
    }
  */

  // Start with slow start (default for every connection)
  if(connection->mode == SLOW_START){

    // If package is dropped half the slow start treshold and 
    if(package_dropped){
      connection->package_dropped = 0;
      connection->SS_treshold = connection->window_size / 2;  
      if(connection->SS_treshold < 2){
        connection->SS_treshold = 2;
      }
      // Not specified. But if package is dropped start over.
      connection->window_size = 1;
      connection->addition_offset = 0;
    }  

    // In the slow start phase at every ACK increase the window by 1 until
    // you hit the Slow Start Treashold.
    if (connection->window_size < connection->SS_treshold){
        connection->window_size++;
    }

    // When you hit the SS_Treshold switch to congestion avoidance.
    if(connection->window_size == connection->SS_treshold){
       connection->mode = C_AVOIDANCE;
    }

  }

  // In congestion avoidance only increase when the full window has transferred.
  if(connection->mode == C_AVOIDANCE){
    connection->addition_offset += (1 / (double)connection->window_size);
    if(connection->addition_offset >= 1){
      connection->window_size++;
      connection->addition_offset = 0;
    }
    //If package is dropped switch to Slow start once again due to FAST RETRANSMIT.
    if(package_dropped){
      connection->package_dropped = 0;
      connection->SS_treshold = connection->window_size / 2;  
      if(connection->SS_treshold < 2){
        connection->SS_treshold = 2;
      }
      connection->window_size = 1;
      connection->mode = SLOW_START;
      connection->addition_offset = 0;
    }  
  }

}

/*
###############################################################################
############################   Package Handling   #############################
###############################################################################
*/

// Starts the request by sending all peers a WHOHAS request.
void send_WHOHAS_to_all(char *hash_values){

    // Create the who has package.
    packet_t *WHOHAS_packet = create_package(WHOHAS);

    // Load the payload with hash values.
    strcpy(WHOHAS_packet->payload, hash_values);

    struct bt_peer_s* active_peer = config.peers;

    // Send who has to all peers but ones self.
    while(active_peer != NULL) {
        if (active_peer->id != config.identity) {
            //Send packet defintion can be found in the helper fucntions.
            send_packet(WHOHAS_packet, config.sock);
        }
        active_peer = active_peer->next;
    }
}

// Handles the WHOHAS request.
void handle_WHOHAS(const char **requested_chunks_list, int list_len){
    
    packet_t *IHAVEPACKET = create_package(IHAVE);
    
    char *chunk_values = 0;

    // handle_file definition can be found in helper funcitons.
    chunk_values = handle_file(chunk_values, config.has_chunk_file);    


    const char *chunks_list[20];
    int index = 0;
    while ((chunks_list[index] = strsep(&chunk_values, "\n")) != NULL){
        index++;
    }

    // Compares the given hash values to Node.haschunks hashes.
    // Uses pointer math to remove the id number from the hash.
    int present_chunks_list_len = index - 1;
    int i, j;
    for (i = 0; i < list_len; ++i){
      requested_chunks_list[i]++;
      requested_chunks_list[i]++;
      for (j = 0; j < present_chunks_list_len; ++j){
        chunks_list[j]++;
        chunks_list[j]++; 

        if(strcmp(requested_chunks_list[i], chunks_list[j]) == 0){
          chunks_list[j]--;
          chunks_list[j]--;
          // When a hash match is detected respond with the given hash.
          strcpy(IHAVEPACKET->payload, chunks_list[j]);
          send_packet(IHAVEPACKET, config.sock);
        }
        chunks_list[j]--;
        chunks_list[j]--;
      }
      requested_chunks_list[i]--;
      requested_chunks_list[i]--;
    }
    if (IHAVEPACKET == 0){
      free(IHAVEPACKET);
    }
}

void handle_GET(packet_t *packet){

  char *data_location;
  size_t len = 0;


  // Get the location of the data file from Node.masterchunks.
  FILE *file_descriptor;
  if((file_descriptor = fopen(config.chunk_file, "r")) == 0) printf("Failed to open file.\n");
  getline(&data_location, &len, file_descriptor);
  data_location = data_location + 6;
  trim_string(data_location);
  fclose(file_descriptor);

  // Figures out which segment of the hash to read.
  int chunk_sequence = atoi(&packet->payload[0]);

  // Opens the data file.
  FILE* data_file = fopen(data_location, "rb");
    if(data_file == NULL){
      printf("Failed to open the data numbered <%d>, in path <%s>\n", chunk_sequence, data_location);
      return;
    }

  // Loads the a chunk size data from the given offset to the data_buffer.
  char data_buffer[BT_CHUNK_SIZE];
  int offset = chunk_sequence * BT_CHUNK_SIZE;
  fseek(data_file, offset, SEEK_SET);
  int read_size = fread(data_buffer, sizeof(char), BT_CHUNK_SIZE, data_file);
  if(read_size != BT_CHUNK_SIZE){
    printf("Failed to load the data numbered <%d>, in path <%s>\n", chunk_sequence, data_location);
    return;
  }
  printf("Load chunk <%d> from file <%s> successfully\n", chunk_sequence, data_location);
  fclose(data_file);

  int counter = 0;
  int seq_num = 0;


  // Creates a connection
  connection_t *connection = add_connection();
 

  //while(counter < 1500){ -> used while creating the mock graph
  
  // Divides the data in the buffer to DATA_packages. 
  // Assigns the data_packages to the freshly opened connection.
  while(counter < MAX_CHUNKS){
    packet_t *DATAPACKET = create_DATA_package(seq_num, connection->id);
    char temp[1400];
    strncpy(temp, data_buffer, 1400);
    memmove(data_buffer, data_buffer + 1400, sizeof(data_buffer) - sizeof(*data_buffer));
    strcpy(DATAPACKET->payload, temp);
    enQueue(connection->data_queue, DATAPACKET);
    counter++;
    seq_num++;
    }

  // Starts the sliding window to initilize the data transfer.
  start_sliding_window(connection->id);
}


// Handles the ACK packages.
void handle_ACK(packet_t *packet){

  // Retrieves the connection that the package is assigned to.
  connection_t *connection = retrieve_conection(packet->connection_id);


  // Drop the package if 
  if (connection->last_ack == -1){
    connection->last_ack = packet->packet_header.ack_num;
  }

  if(connection->last_ack == packet->packet_header.ack_num){
    connection->same_ack_counter++;
    if(connection->same_ack_counter == 3){
      connection->same_ack_counter = 0;
      connection->package_dropped = 1;
    }
  }

  // Implements the necesary congestion controlls.
  congestion_controll(connection);

  // Writes the result of the congestion control to the problem2-peer.txt file.
  FILE *program_output = fopen(PROGRAM_OUTPUT_PATH, "a+");
  fseek(program_output, 0, SEEK_END);

  struct timeval current_time;
  gettimeofday(&current_time, NULL);
  int time_elapsed =  current_time.tv_usec - connection->start_time.tv_usec;
  fprintf(program_output, "id%d %d %d\n",connection->id, time_elapsed, connection->window_size);
  fclose(program_output);


  //check whether that is in the buffer, if so delete it.
  remove_packet_from_buffer(packet, connection);


  // Deals with sending the request/ack for the next DATA package.
  int ack_num = packet->packet_header.ack_num;
  if (connection->last_packet_acked <= ack_num){
    connection->last_packet_acked++; 
  }

  packet_t *data_packet = deQueue(connection->data_queue);
    if (data_packet == NULL){
      printf("Data not found and returned in retreive ACK\n");
      return;
  }
  
  add_packet2buffer(data_packet, connection);
  
  send_packet(data_packet, config.sock);

  // start timer
  // after five seconds if it is still in the buffer resend it.
  connection->last_packet_sent++;

  gettimeofday(&current_time, NULL);
  time_elapsed =  current_time.tv_usec - connection->start_time.tv_usec;
  while(time_elapsed < 5000){
    time_elapsed =  current_time.tv_usec - connection->start_time.tv_usec;
  }


  int package_timeout = check_packet_in_buffer(data_packet, connection);
  if(package_timeout){
    handle_ACK(data_packet);
  }

  printf("Send the package with seq_num |%d| in connection |%d|.\n", data_packet->packet_header.seq_num, connection->id);
  //free(data_packet);
}

// Given a package decides how to react.
void respond_packet(packet_t *packet){

    // Packet denotes the received package.
    switch(packet->packet_header.packet_type) {

        case (WHOHAS):
            printf("Received WHOHAS.\n");
            
            char *payload = packet->payload;
            const char *chunks_list[20];

            int index = 0;
            while ((chunks_list[index] = strsep(&payload, "\n")) != NULL){
                index++;
            }
            int chunks_list_len = index - 1;
            handle_WHOHAS(chunks_list, chunks_list_len);

            break;

       case (IHAVE):
            printf("Received IHAVE.\n");
            packet_t *GETPACKET = create_package(GET);
            strcpy(GETPACKET->payload, packet->payload);
            send_packet(GETPACKET, config.sock);

            free(GETPACKET);
            break;

       case (GET):
            printf("Received GET.\n");
            
            handle_GET(packet);
            FILE *create_file;
            // config.output doesnt work. Creates file so others can append.
            create_file = fopen("/tmp/newB.tar", "w");
            fclose(create_file);   

            break;

       case (DATA):

          //printf("");
          printf("Received DATA\n");
          FILE *file_descriptor;

          // The config file doesnt work.
          // file_descriptor = fopen(config.output_file, "a+");
          file_descriptor = fopen("/tmp/newB.tar", "a+");

          if (!file_descriptor){
              printf("Failed to open file.\n");
              return;
          }

          fseek(file_descriptor, 0, SEEK_END); 
          fprintf(file_descriptor, "%s", packet->payload);
          fclose(file_descriptor);

          packet_t *ACKPACKET = create_ACK_package(packet->packet_header.seq_num , 
                                                   packet->connection_id);
          send_packet(ACKPACKET, config.sock);

          free(ACKPACKET);
          break;

       case (ACK):
          handle_ACK(packet);
          break;

       case (DENIED):
            printf("Received DENIED\n");
            break;

       default:
            printf("Packet type is corrupted!\n");
            return;
    }

}


/*
###############################################################################
##########################   Pre Defined Functions   ##########################
###############################################################################
*/
int main(int argc, char **argv) {
  bt_init(&config, argc, argv);

  DPRINTF(DEBUG_INIT, "peer.c main beginning\n");

#ifdef TESTING
  config.identity = 1; // your group number here
  strcpy(config.chunk_file, "chunkfile");
  strcpy(config.has_chunk_file, "haschunks");
#endif

  bt_parse_command_line(&config);

#ifdef DEBUG
  if (debug & DEBUG_INIT) {
    bt_dump_config(&config);
  }
#endif

  peer_run(&config);
  return 0;
}

void process_inbound_udp(int sock) {
    packet_t* packet;

    #define BUFLEN 1500
    struct sockaddr_in from;
    socklen_t fromlen;
    char buf[BUFLEN];

    fromlen = sizeof(from);
    spiffy_recvfrom(sock, buf, BUFLEN, 0, (struct sockaddr *) &from, &fromlen);

    // Packet is received and formatted correctly.
    packet = receive_packet(buf);

    if (packet == 0) {
        printf("The packet couldnt be received.\n");
        return;
  }

   // Based on the which packet it is the required response is given.
   respond_packet(packet);


}

//In here send the who has command. To everyone in my peer.map WHOHAS Request.
void process_get(char *chunkfile, char *outputfile) {

    char *hash_values = 0;
    hash_values = handle_file(hash_values, chunkfile);
    send_WHOHAS_to_all(hash_values);

    }

//If its get, it goes to process_get.
void handle_user_input(char *line, void *cbdata) {
  char chunkf[128], outf[128];

  bzero(chunkf, sizeof(chunkf));
  bzero(outf, sizeof(outf));

  if (sscanf(line, "GET %120s %120s", chunkf, outf)) {
    if (strlen(outf) > 0) {
      process_get(chunkf, outf);
    }
  }
}

void peer_run(bt_config_t *config) {
  int sock;
  struct sockaddr_in myaddr;
  fd_set readfds;
  struct user_iobuf *userbuf;


  FILE *program_output;
  program_output = fopen(PROGRAM_OUTPUT_PATH, "w");
  fclose(program_output);


  if ((userbuf = create_userbuf()) == NULL) {
    perror("peer_run could not allocate userbuf");
    exit(-1);
  }

  if ((sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_IP)) == -1) {
    perror("peer_run could not create socket");
    exit(-1);
  }

  // Set the socket on config.
  config->sock = sock;

  bzero(&myaddr, sizeof(myaddr));
  myaddr.sin_family = AF_INET;
  myaddr.sin_addr.s_addr = htonl(INADDR_ANY);
  myaddr.sin_port = htons(config->myport);

  if (bind(sock, (struct sockaddr *) &myaddr, sizeof(myaddr)) == -1) {
    perror("peer_run could not bind socket");
    exit(-1);
  }

  //here is the config.
  spiffy_init(config->identity, (struct sockaddr *)&myaddr, sizeof(myaddr));

  while (1) {
    int nfds;
    FD_SET(STDIN_FILENO, &readfds);
    FD_SET(sock, &readfds);

    nfds = select(sock+1, &readfds, NULL, NULL, NULL);

    // When one peer gets STDIN, it sends out WHOHAS to all in bt_something.peers.addr. it comes here, responds in IHAVE etc...
    if (nfds > 0) {
      if (FD_ISSET(sock, &readfds)) {
	     process_inbound_udp(sock);
      }

      //listening to STDIN and sockets. Think about passing in config. GET comes in here.
      if (FD_ISSET(STDIN_FILENO, &readfds)) {
	process_user_input(STDIN_FILENO, userbuf, handle_user_input,
			   "Currently unused");
      }
    }
  }
}
