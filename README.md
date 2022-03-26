# Cache-Simulator
A Java program that simulates the cache in a CPU. The user is able to initialize the cache with the customizable parameters cache size (8-256B), 
data block size, associativity (1, 2, or 4-way), replacement policy (random/LRU), write hit policy (write-through/write-back), 
and write miss policy (write-allocate/no write-allocate). Once initialized, the user is able to perform several different operations
such as reading/writing to the cache, flushing the cache, viewing the cache/memory, and dumping the cache/memory.

# Usage
After compiling and executing cachesimulator.java, the following dialogue appears: 

![](https://i.imgur.com/xRpcVx0.png)

To proceed, the user should enter the starting and ending addresses that define the desired RAM space, in hex, using the following command: "init-ram 0xYW 0xZJ"
where Y, W, Z, and J are user defined. To initialize successfully, the following constraints must be satisfied: 0xYW < 0xZJ, 0xZJ <= 0xFF.

Next, the following dialogue will appear, indicating that the RAM was successfully initialized and that more user parameters are requested:

![](https://i.imgur.com/vuReJg7.png)

Aftering entering the requested parameter, the next parameter will be requested, like so:

![](https://i.imgur.com/FyiRA8N.png)

This is where the user will be able to configure the cache size in bytes (8-256), data block size, associativity (1, 2, or 4), replacement policy (1 for Random and 2 for Least Recently Used), write hit policy (1 for write-through and 2 for write-back), and write miss policy (1 for write-allocate and 2 for no write-allocate).

After successfully entering all parameters, the Cache simulator menu should appear:

![](https://i.imgur.com/Jz1nYyA.png)
