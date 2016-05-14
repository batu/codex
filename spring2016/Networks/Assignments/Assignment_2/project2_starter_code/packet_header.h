#include <sys/time.h>
/*
 * packetheader.h
 *
 * Initial Author: Batu Aytemiz
 *  (Spring 2016)
 * Define the package header datastructure that will be used throughout the project.
 *
 */

/*
WHOHAS  = 0
IHAVE   = 1
GET     = 2
DATA    = 3
ACK     = 4
DENIED  = 5
*/

typedef struct packet_header_t{

    short   magic_num;         //: 16; 2
    char    version_num;       //: 8; 1
    char    packet_type;       //: 8; 1
    short   header_len;        //: 16; 2
    short   packet_len;        //: 16; 2
    short   seq_num;           //: 16; 2
    int32_t ack_num;           //: 32; 4

}packet_header_t;

typedef struct packet_t{
    packet_header_t packet_header;
    int connection_id;
    char payload[1400];
    struct timeval send_time;


}packet_t;
