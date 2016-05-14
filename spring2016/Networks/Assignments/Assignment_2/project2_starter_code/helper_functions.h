#include <sys/time.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SS_TRESH 64
#define MAGIC_NUMBER 15441
#define CHUNK_DATA_SIZE (512*1024)
#define MAX_CHUNKS 375 // (512*1024 / 1400);

#define WHOHAS  0
#define IHAVE   1
#define GET     2
#define DATA    3
#define ACK     4
#define DENIED  5

#include "connection.h"

/*
 * helper functions.h
 *
 * Initial Author: Batu Aytemiz
 *  (Spring 2016)
 * Helper functions to manage the data.
 *
 */

// Trims the final newline.
void trim_string(char* string);

// Endian swaps for 64 bit machines.
void endianSwap_Host_to_Network(packet_t *packet);
void endianSwap_Network_to_Host(packet_t *packet);

// open a file and reads whatever is into buffer.
char* handle_file(char *buffer, char *path);

void add_packet2buffer(packet_t *packet, connection_t *connection);
void remove_packet_from_buffer(packet_t *packet, connection_t *connection);
int check_packet_in_buffer(packet_t *packet, connection_t *connection);

// Receives the bit stream and casts it into to the package format.
packet_t* receive_packet(char *buf);