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

After entering the requested parameter, the next parameter will be requested, like so:

![](https://i.imgur.com/FyiRA8N.png)

This is where the user will be able to configure the cache size in bytes (8-256), data block size, associativity (1, 2, or 4), replacement policy (1 for Random and 2 for Least Recently Used), write hit policy (1 for write-through and 2 for write-back), and write miss policy (1 for write-allocate and 2 for no write-allocate).

After successfully entering all parameters, the Cache simulator menu should appear where the user is given the option to perform a variety of operations on the cache or the memory:

![](https://i.imgur.com/Jz1nYyA.png)

The following describes and shows an example of each of the operations.

# Operation Descriptions and Examples

### cache-read
In order to perform a cache-read, the user should enter the following command: "cache-read 0xJP" where 0xJP defines the desired address in hex. The following is an example attempting to read from the address 0x18:

![](https://i.imgur.com/62SUOzv.png)

### cache-write
For a cache-write, the user should enter the following command: "cache-write 0xJP 0xTW" where 0xTW is the data to be written to address 0xJP. The following is an example attempting to write 0xAB to address 0x10:

![](https://i.imgur.com/8G94WU4.png)

### cache-flush
For the cache-flush command, the user simply needs to enter the command "cache-flush" with no additional parameters. Should the user wish to verify that the cache was successfully flushed, the user can utilize the "cache-view" command to view the cache. After entering the command, a message indicating the cache was successfully cleared should be shown, like so:

![](https://i.imgur.com/jqWp5PS.png)

### cache-view
For the cache-view command, the user simply needs to enter the command "cache-view". The cache in its current state should then be printed to the console, like so:

![](https://i.imgur.com/CQcg3Yv.png)

### memory-view
To view the memory, the user simply needs to enter the command "memory-view". The current state of the memory should then be printed to the console, like so:

![](https://i.imgur.com/nKsEs3t.png)

### cache-dump
To perform a cache-dump, the user should enter the command "cache-dump" with no additional parameters. This will output the current state of the cache to a file named cache.txt in the working directory. It should also print out the contents of cache.txt to the console, like so:

![](https://i.imgur.com/T83obw3.png)

### memory-dump
To dump the memory to a file, the user should enter the command "memory-dump" with no additional parameters. This will output the current state of the memory to a file named ram.txt in the working directory. It will also output the contents of ram.txt to the console, like so (truncated as output is very long):

![](https://i.imgur.com/keS1xMx.png)

### quit
Finally, should the user wish to exit the Cache Simulator, they need only enter the command "quit". This will immediately fully exit the program.
