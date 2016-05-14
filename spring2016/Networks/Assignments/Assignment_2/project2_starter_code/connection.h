#include "queue.h"
#include <sys/time.h>

/*
 *
 * Initial Author: Batu Aytemiz
 *  (Spring 2016)
 * 	Define connections
 *
 */
#define MAX_WINDOW_SIZE 64



//  for mode:
//  #define SLOW_START 0
//	#define C_AVOIDANCE 1
//	#define F_RETRANSMIT 2

typedef struct connection_t{

	// Proposed solution to the TCP problems I am having.
	// I sadly didnt have time to implement this.

	// After you send packets put them in the buffer. Remove them from the buffer when you get an ack for the 
	// given packet.  This helps with reordering and resending.
	packet_t *send_buffer[MAX_WINDOW_SIZE * 2];


	// Whenever you get the same ack increment the same ack counter. 
	// If the same ack counter reaches three assume the data package is dropped.
	int same_ack_counter;
	int last_ack;

	// When an ack is received record its time. Every second iterate through the connection lists and
	// check their last ack timer with the current time. If any last ack timer has been over TIMEOUT_TIME
	// assume the package is dropped and send the given packet from the packet buffer.
	struct timeval last_ack_timer;
	float timeout_time;
	
	/*
	*/
	int package_dropped;

    // data queue where the packets are stored.
	Queue *data_queue;

	// connection id
    int id;

    // The window size and required TCP values.
    int window_size;
    int last_packet_acked;
	int last_packet_sent;
	int last_packet_available;
	int seq_num_buffer[MAX_WINDOW_SIZE];

	// congestion controll values and slow start treshold.
	int mode;
	int SS_treshold;
	struct timeval start_time;
	double addition_offset;

	// next connection for the list.
	struct connection_t *next;

}connection_t;

 typedef struct connection_list_t{
 	connection_t *connections;
}connection_list_t;
