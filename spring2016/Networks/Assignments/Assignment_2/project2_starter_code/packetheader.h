
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

/*
Add a max payload.
convert it back to the hexadecimal
check the code.
strip the first two chars.

use make chunks
check it in master chunks

char[70][20]
union

*/


typedef struct packet_header_t{

    short magic_num;         //: 16; 2
    char  version_num;       //: 8; 1
    char  packet_type;       //: 8; 1
    short header_len;        //: 16; 2
    short packet_len;        //: 16; 2
    short seq_num;           //: 16; 2
    int32_t   ack_num;       //: 32; 4

    // however everything below gets corrupted





    /*
    unsigned int magic_num   : 2;  //It should be 15441
    unsigned int version_num : 1; // It should be 1.
    unsigned int packet_type : 1;
    unsigned int header_len  : 2;
    unsigned int total_packet_len : 2;
    unsigned int seq_num : 2;
    unsigned int ack_num : 4;
    */
}packet_header_t;

typedef struct packet_t{
    packet_header_t packet_header;
    char payload[1400];

}packet_t;
