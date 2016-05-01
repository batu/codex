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

// User created content.
#include "packetheader.h"

#define MAGIC_NUMBER 15441

#define WHOHAS  0
#define IHAVE   1
#define GET     2
#define DATA    3
#define ACK     4
#define DENIED  5

void peer_run(bt_config_t *config);

// Define the peer scoped config. It will be used in get process. Should change it to be passed through the function.
bt_config_t config;

int main(int argc, char **argv) {
  // moved it to global
  //bt_config_t config;

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

void endianSwap_Host_to_Network(packet_t *packet){
    packet->packet_header.magic_num     = htons(packet->packet_header.magic_num);

    // No need to convert these.
    // packet->packet_header.version_num   = htons(packet->packet_header.version_num);
    // packet->packet_header.packet_type   = htons(packet->packet_header.packet_type);
    packet->packet_header.header_len    = htons(packet->packet_header.header_len);
    packet->packet_header.seq_num       = htons(packet->packet_header.seq_num);
	packet->packet_header.ack_num       = htonl(packet->packet_header.ack_num);
    packet->packet_header.packet_len    = htons(packet->packet_header.packet_len);
}

void endianSwap_Network_to_Host(packet_t *packet){
    packet->packet_header.magic_num     = ntohs(packet->packet_header.magic_num);
    // No need to convert these.
    // packet->packet_header.version_num   = ntohs(packet->packet_header.version_num);
    // packet->packet_header.packet_type   = ntohs(packet->packet_header.packet_type);
    packet->packet_header.header_len    = ntohs(packet->packet_header.header_len);
    packet->packet_header.seq_num       = ntohs(packet->packet_header.seq_num);
	packet->packet_header.ack_num       = ntohl(packet->packet_header.ack_num);
    packet->packet_header.packet_len    = ntohs(packet->packet_header.packet_len);
}

packet_t* create_package(int packet_type){
    struct packet_t *packet;

    packet = (struct packet_t *)malloc(sizeof(struct packet_t));

    strcpy(packet->packet_header.test, "ABCDEFG");

    packet->packet_header.magic_num = MAGIC_NUMBER;
    packet->packet_header.version_num = 1;
    packet->packet_header.packet_type = packet_type;
    packet->packet_header.header_len = 16;
    packet->packet_header.seq_num = 1;
	packet->packet_header.ack_num = 0;
    packet->packet_header.packet_len = 60;

/*
    printf("Printing the size of CHAR - ABCDEFG |%lu|\n", sizeof(packet->packet_header.test) );
    printf("Printing the size of magic num |%lu|\n", sizeof(packet->packet_header.magic_num) );
    printf("Printing the size of version num |%lu|\n", sizeof(packet->packet_header.version_num) );
    printf("Printing the size of packe type |%lu|\n", sizeof(packet->packet_header.packet_type) );
    printf("Printing the size of header len |%lu|\n", sizeof(packet->packet_header.header_len) );
    printf("Printing the size of seq num |%lu|\n", sizeof(packet->packet_header.seq_num) );
    printf("Printing the size of ack num |%lu|\n", sizeof(packet->packet_header.ack_num) );
    printf("Printing the size of packet_len |%lu|\n", sizeof(packet->packet_header.packet_len) );
*/

    //endianSwap_Host_to_Network(packet);

    return packet;
}

packet_t* receive_payload(char *buf){

    // Convert the payload from char buf to the packet data structure.
    packet_t *packet = (packet_t*) buf;
    //endianSwap_Network_to_Host(packet);
    packet_header_t *header = &packet->packet_header;


    printf("Printing the payload |%s|\n",packet->payload );





    if(header->magic_num != MAGIC_NUMBER){
        printf("Magic number is corrupted!\n");
        return NULL;
    }

    switch(header->packet_type) {
        case (WHOHAS):
            printf("Received WHOHAS.\n");
            break;
       case (IHAVE):
            printf("Received IHAVE.\n");
            break;
       case (GET):
            printf("Received GET.\n");
            break;
       case (DATA):
            printf("Received DATA.\n");
            break;
       case (ACK):
            printf("Received ACK.\n");
            break;
       case (DENIED):
            printf("Received DENIED\n");
            break;
       default:
            printf("Packet type is corrupted!\n");
            return NULL;
    }

    return packet;

}


void send_WHOHAS_to_all(char *hash_values){

    packet_t* WHOHAS_packet = create_package(WHOHAS);
    //strcpy(WHOHAS_packet->payload, hash_values);

    strcpy(WHOHAS_packet->payload, hash_values);

    // The way I am assigning the test. Also tried the strcpy with char[MAX_LEN]

    // set the head.
    struct bt_peer_s* active_peer = config.peers;

    /*
        VALUES HARD CODED - CHANGE IT IN NEXT COMMIT.
    */


    while(active_peer != NULL) {
        if (active_peer->id != config.identity) {
            printf("In here\n");
            // fix this
            //spiffy_sendto(active_peer->addr.sin_addr.s_addr, WHOHAS_packet, sizeof(packet_t), 0, (struct sockaddr *) &config.peers[0].addr, sizeof(config.peers[0].addr));
            spiffy_sendto(config.sock, WHOHAS_packet, sizeof(WHOHAS_packet), 0, (struct sockaddr *) &config.peers[0].addr, sizeof(config.peers[0].addr));
        }
        active_peer = active_peer->next;
    }
}


void process_inbound_udp(int sock) {
    packet_t* packet;

    #define BUFLEN 1500
    struct sockaddr_in from;
    socklen_t fromlen;
    char buf[BUFLEN];

    fromlen = sizeof(from);
    spiffy_recvfrom(sock, buf, BUFLEN, 0, (struct sockaddr *) &from, &fromlen);

    packet = receive_payload(buf);

    if (packet == 0) {
        printf("The packet couldnt be received.\n");
        return;
  }

    printf("The magic # is: |%d|\n", packet->packet_header.magic_num);


  /*

        IMPLEMENT I HAVE HERE.

  */

}
//In here send the who has command. To everyone in my peer.map WHOHAS Request.
void process_get(char *chunkfile, char *outputfile) {

    FILE *file_descriptor;
    file_descriptor = fopen(chunkfile, "r");

    fprintf(file_descriptor, "This is testing for fprintf...\n");

    if (!file_descriptor){
        printf("Failed to open file.\n");
        return;
    } else{
        printf("Opened the file at %s.\n", chunkfile);
    }

    char *hash_values = NULL;

    // CODE FROM STACK OVERFLOW
    // --------------------------------------------------------------
    if (fseek(file_descriptor, 0L, SEEK_END) == 0) {
        /* Get the size of the file. */
        long bufsize = ftell(file_descriptor);

        if (bufsize == -1)
            fputs("Buffsize is not correct.\n", stderr);

        /* Allocate our buffer to that size. */
        hash_values = malloc(sizeof(char) * (bufsize + 1));

        /* Go back to the start of the file. */
        if (fseek(file_descriptor, 0L, SEEK_SET) != 0) { /* Error */ }

        /* Read the entire file into memory. */
        size_t newLen = fread(hash_values, sizeof(char), bufsize, file_descriptor);
        if (newLen == 0) {
            fputs("Error reading file\n", stderr);
        } else {
            hash_values[newLen++] = '\0'; /* Just to be safe. */
        }
    }
    fclose(file_descriptor);
    // --------------------------------------------------------------

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
