#include <stdlib.h>
#include <stdio.h>
#include "packet_header.h"

/*
Initial Author: Batu Aytemiz
 *  (Spring 2016)
 * 	Define the queue data structure that will be used throughout the project.
    Queue basic skelaton code taken from :
    http://geeksquiz.com/queue-set-2-linked-list-implementation/

*/


// A linked list (LL) node to store a queue entry
typedef struct QNode
{
    struct packet_t *packet;
    struct QNode *next;
} QNode;

// The queue, front stores the front node of LL and rear stores ths
// last node of LL
typedef struct Queue_t
{
    struct QNode *front, *rear;
} Queue;

Queue* createQueue();
void enQueue(Queue *q, packet_t *packet);
packet_t *deQueue(Queue *q);
int isQueueEmpty(Queue *q);
