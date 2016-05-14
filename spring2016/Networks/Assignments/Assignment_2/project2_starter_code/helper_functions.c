#include "helper_functions.h"



void trim_string(char* string){
    char* newlineLocation = strstr(string, "\n");
    if (newlineLocation != NULL){
        strncpy(newlineLocation -1, "\0", 1);
    }
}

// Host to Network and Network to Host funcitons swap the from big/little endian to ensure there are no problems on 64bit systems.
void endianSwap_Host_to_Network(packet_t *packet){
    packet->packet_header.magic_num     = htons(packet->packet_header.magic_num);
    packet->packet_header.header_len    = htons(packet->packet_header.header_len);
    packet->packet_header.seq_num       = htons(packet->packet_header.seq_num);
	packet->packet_header.ack_num       = htonl(packet->packet_header.ack_num);
    packet->packet_header.packet_len    = htons(packet->packet_header.packet_len);
}

void endianSwap_Network_to_Host(packet_t *packet){
    packet->packet_header.magic_num     = ntohs(packet->packet_header.magic_num);
    packet->packet_header.header_len    = ntohs(packet->packet_header.header_len);
    packet->packet_header.seq_num       = ntohs(packet->packet_header.seq_num);
	packet->packet_header.ack_num       = ntohl(packet->packet_header.ack_num);
    packet->packet_header.packet_len    = ntohs(packet->packet_header.packet_len);
}

char* handle_file(char *buffer, char *path){

    FILE *file_descriptor;
    file_descriptor = fopen(path, "rb");

    if (!file_descriptor){
        printf("Failed to open file.\n");
        return NULL;
    } 

    // CODE FROM STACK OVERFLOW
    // --------------------------------------------------------------

    if (fseek(file_descriptor, 0L, SEEK_END) == 0) {
        /* Get the size of the file. */
        long bufsize = ftell(file_descriptor);

        if (bufsize == -1)
            fputs("Buffsize is not correct.\n", stderr);

        /* Allocate our buffer to that size. */
        buffer = malloc(sizeof(char) * (bufsize + 1));

        /* Go back to the start of the file. */
        if (fseek(file_descriptor, 0L, SEEK_SET) != 0) { /* Error */ }

        /* Read the entire file into memory. */
        size_t newLen = fread(buffer, sizeof(char), bufsize, file_descriptor);
        if (newLen == 0) {
            fputs("Error reading file\n", stderr);
        } else {
            buffer[newLen++] = '\0'; /* Just to be safe. */
        }
    }
    fclose(file_descriptor);
    // --------------------------------------------------------------
    return buffer;
}


// receives the bit stream and creates the correct package.
packet_t* receive_packet(char *buf){

    // Convert the payload from char buf to the packet data structure.
    packet_t *packet = (packet_t*) buf;

    // Endian swap if needed.
    //endianSwap_Network_to_Host(packet);
    
    packet_header_t *header = &packet->packet_header;
    if(header->magic_num != MAGIC_NUMBER){
        printf("Magic number is corrupted!\n");
        return NULL;
    }
    return packet;
}

// add packet to buffer
void add_packet2buffer(packet_t *packet, connection_t *connection){
    int window_size = connection->window_size;
    int i = 0;
    for(i = 0; i < window_size; i++){
        if(connection->send_buffer[i] == NULL){
            connection->send_buffer[i] = packet;
            if(packet != NULL){
                // printf("Packet added to the buffer in location |%d| in connection |%d| with seq_num |%d|.\n", i,
                //     connection->id, packet->packet_header.seq_num); 
            }
            return;
        }
    }
    printf("Connection buffer is full!\n");
}

void remove_packet_from_buffer(packet_t *packet, connection_t *connection){
    int window_size = connection->window_size;
    int i = 0;
    for(i = 0; i < window_size; i++){
        if(connection->send_buffer[i] != NULL){
            if(connection->send_buffer[i]->packet_header.seq_num == packet->packet_header.ack_num){
                connection->send_buffer[i] = NULL;
                // printf("Packet in location |%d| in connection |%d| with seq_num |%d| has been deallocated.\n", i,
                //     connection->id, packet->packet_header.ack_num);
                return;
            }
        }
    }
    printf("Packet can not be found!\n");
    return;
}


int check_packet_in_buffer(packet_t *packet, connection_t *connection){
    int window_size = connection->window_size;
    int i = 0;
    for(i = 0; i < window_size; i++){
        if(connection->send_buffer[i] != NULL){
            if(connection->send_buffer[i]->packet_header.seq_num == packet->packet_header.ack_num){
                return 1;
            }
        }
    }
    return 0;
}

