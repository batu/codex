#include <stdlib.h>
#include <stdio.h>
#include "queue.h"

/*

    Queue basic skelaton code taken from :
    http://geeksquiz.com/queue-set-2-linked-list-implementation/

*/


int isQueueEmpty(Queue *q){
    if (q->front == NULL)
       return 0;
    return 1;
}

// A utility function to create a new linked list node.
QNode* newNode(packet_t *packet){
    QNode *temp = (QNode*)malloc(sizeof(QNode));
    temp->packet = packet;
    temp->next = NULL;
    return temp;
}

// A utility function to create an empty queue
Queue* createQueue(){
    Queue *q = (Queue*)malloc(sizeof(Queue));
    q->front = q->rear = NULL;
    return q;
}

// The function to add a key k to q
void enQueue(Queue *q, packet_t *packet){
    // Create a new LL node
    QNode *temp = newNode(packet);

    // If queue is empty, then new node is front and rear both
    if (q->rear == NULL)
    {
       q->front = q->rear = temp;
       return;
    }

    // Add the new node at the end of queue and change rear
    q->rear->next = temp;
    q->rear = temp;
}

// Function to remove a key from given queue q
packet_t *deQueue(Queue *q){
    // If queue is empty, return NULL.
    if (q->front == NULL)
       return NULL;

    // Store previous front and move front one node ahead
    struct QNode *temp = q->front;
    q->front = q->front->next;

    // If front becomes NULL, then change rear also as NULL
    if (q->front == NULL)
       q->rear = NULL;
    return temp->packet;
}

