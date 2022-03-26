# Cache-Simulator
A Java program that simulates the cache in a CPU. The user is able to initialize the cache with the customizable parameters cache size (8-256B), 
data block size, associativity (1, 2, or 4-way), replacement policy (random/LRU), write hit policy (write-through/write-back), 
and write miss policy (write-allocate/no write-allocate). Once initialized, the user is able to perform several different operations
such as reading/writing to the cache, flushing the cache, viewing the cache/memory, and dumping the cache/memory.

# Usage
After compiling and executing cachesimulator.java, the following dialogue appears: 

![](https://i.imgur.com/OvTvqAO.png)

To proceed, the user should enter the starting and ending addresses that define the desired RAM space, in hex, using the following command: "init-ram 0xYW 0xZJ"
where Y, W, Z, and J are user defined.

Constraints: 0xYW < 0xZJ, 0xZJ <= 0xFF
