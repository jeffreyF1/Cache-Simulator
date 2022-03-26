// File: cachesimulator.java
// Author(s): Jeffrey Formica
// Date: 12/06/2021
// Section: 506
// E-mail(s): formica.jeffrey@tamu.edu
// Description: This file implements the entire cache simulator. Based on user input, a fake cache is simulated; the user can perform several cache related functions on this simulated cache, such as reading, writing, flushing, etc.

import java.io.*;
import java.util.*;

public class cachesimulator
{
	public static Scanner userInput; ///< Used to read input from the user.
	public static long RAM[]; ///< The data stored in main memory. Initialized based on user input in the initializeRAM() function.
	public static String cache[][]; ///< Represents the cache. First index represents the set index, the second index represents the line index within that set. Configured in configureCache().
	public static int cacheUse[][]; ///< Represents the usage of the lines within each set of the cache by an integer. Uses same indices as cache; higher number of use in each line = older = evict first.
	public static int cacheSize; ///< The size of the cache; configured in the configureCache() function.
	public static int dataBlockSize; ///< The amount of bytes per data block within the cache. Configured in the configureCache() function.
	public static int assoc; ///< The number of lines per set within the cache. Configured in the configureCache() function.
	public static int repPolicy; ///< The replacement policy of the cache. Configured in the configureCache() function. 1 - random replacement, 2 - least recently used.
	public static int writeHPolicy; ///< The write hit policy of the cache. Configured in configureCache(). 1 - write-through, 2 - write-back.
	public static int writeMPolicy; ///< The write miss policy of the cache. Configured in configureCache(). 1 - write-allocate, 2 - no write-allocate.
	public static int numCacheHits; ///< The total number of successful cache hits, R/W.
	public static int numCacheMisses; ///< The total number of cache misses, R/W.
	
	public static int numTagBits; ///< The number of bits per tag calculated as numTagBits = 8 - (numSetBits + numBlockBits).
	public static int numSetBits; ///< The number of set bits calculated as log_2(cacheSize/(dataBlockSize*assoc)).
	public static int numBlockBits; ///< The number of block offset bits calculated as log_2(dataBlockSize).
	
	/**@brief
	*The main function; calls the functions necessary to simulate the cache.
	*/
	public static void main (String [] args) throws FileNotFoundException, IOException
	{
		System.out.println("*** Welcome to the cache simulator ***");
		if(args.length > 0)
			initializeRAM(args[0]);
		else
			initializeRAM("input.txt");
		
		configureCache();
		
		simulateCache();
	}
	
	/**@brief
	*This function will initialize the RAM using data from a file inputted by the user.
	*@param inputFileName
	*The file name given by the user.
	*/
	public static void initializeRAM(String inputFileName) throws FileNotFoundException
	{
		userInput = new Scanner(System.in);
		System.out.println("initialize the RAM: ");
		String initRAMCommand = userInput.nextLine();
		
		//initial ram command needs to be length of 18, 2 spaces + 8 for each address + init-ram (8 characters) = 18
		while(initRAMCommand.length() != 18)
		{
			System.out.println("invalid RAM parameters. try again.\n");
			System.out.println("initialize the RAM: ");
			initRAMCommand = userInput.nextLine();
		}
		
		//convert the second address in the initial ram command to an integer
		int endRAM = Integer.parseInt(initRAMCommand.substring(16), 16);
		RAM = new long[256]; //RAM will always be size 256, but not all will be initialized
		Scanner inputFileScanner = new Scanner(new File(inputFileName));
		int curPos = Integer.parseInt(initRAMCommand.substring(11, 13), 16);
		while(curPos <= endRAM)
		{
			RAM[curPos++] = Integer.parseInt(inputFileScanner.nextLine(), 16);
		}
		
		System.out.println("RAM successfully initialized!");
	}
	
	/**@brief
	*This function will allow the user to configure the cache with the following parameters: cache size, data block size, associativity, replacement policy,
	*write hit policy, and write miss policy.
	*
	*The valid entries are 8-256 representing the number of bytes for the cache size, 1/2/4 for the associativity (lines per set), 1/2 for the replacement policy (1 corresponding to
	*random replacement, 2 corresponding to least recently used), 1/2 for the write hit policy (1 for write-through, 2 for write-back), and 1/2 for the write miss policy (1 for write-allocate, 2 for no write-allocate).
	*/
	public static void configureCache() throws FileNotFoundException
	{
		System.out.println("configure the cache:");
		
		System.out.print("cache size: ");
		int chosenCacheSize = Integer.parseInt(userInput.next());
		//cache size must be between 8 and 256 bytes
		while(!(chosenCacheSize >= 8 && chosenCacheSize <= 256))
		{
			System.out.println("invalid cache size. try again.\n");
			System.out.print("cache size: ");
			chosenCacheSize = Integer.parseInt(userInput.next());
		}
		cacheSize = chosenCacheSize;
		
		System.out.print("data block size: ");
		dataBlockSize = Integer.parseInt(userInput.next());
		
		System.out.print("associativity: ");
		int chosenAssoc = Integer.parseInt(userInput.next());
		//associativity must be 1, 2, or 4
		while(chosenAssoc != 1 && chosenAssoc != 2 && chosenAssoc != 4)
		{
			System.out.println("invalid associativity. try again.");
			System.out.print("associativity: ");
			chosenAssoc = Integer.parseInt(userInput.next());
		}
		assoc = chosenAssoc;
		
		System.out.print("replacement policy: ");
		int chosenRepPolicy = Integer.parseInt(userInput.next());
		//replacement policy must be 1 (random) or 2 (least recently used)
		while(chosenRepPolicy != 1 && chosenRepPolicy != 2)
		{
			System.out.println("invalid replacement policy. try again.");
			System.out.print("replacement policy: ");
			chosenRepPolicy = Integer.parseInt(userInput.next());
		}
		repPolicy = chosenRepPolicy;
		
		System.out.print("write hit policy: ");
		int chosenWriteHPolicy = Integer.parseInt(userInput.next());
		//write hit policy must be 1 (write-through) or 2 (write-back)
		while(chosenWriteHPolicy != 1 && chosenWriteHPolicy != 2)
		{
			System.out.println("invalid write hit policy. try again.");
			System.out.print("write hit policy: ");
			chosenWriteHPolicy = Integer.parseInt(userInput.next());
		}
		writeHPolicy = chosenWriteHPolicy;
		
		System.out.print("write miss policy: ");
		int chosenWriteMPolicy = Integer.parseInt(userInput.next());
		//write miss policy must be 1 (write-allocate) or 2 (no write-allocate)
		while(chosenWriteMPolicy != 1 && chosenWriteMPolicy != 2)
		{
			System.out.println("invalid write miss policy. try again.");
			System.out.print("write miss policy: ");
			chosenWriteMPolicy = Integer.parseInt(userInput.next());
		}
		writeMPolicy = chosenWriteMPolicy;
		
		System.out.println("cache successfully configured!");
	}
	
	/**@brief
	*This function initializes the cache using the parameters specified in configureCache(). It then begins querying the user for commands they'd like to perform on the cache, options being cache-read, cache-write, cache-flush,
	*cache-view, memory-view, cache-dump, memory-dump, and quit to exit.
	*/
	public static void simulateCache() throws FileNotFoundException, IOException
	{
		// S = C/(BE), log_2(S) = s
		numSetBits = (int)(Math.log(cacheSize/(dataBlockSize*assoc)) / Math.log(2));
		// b = log_2(B)
		numBlockBits = (int)(Math.log(dataBlockSize) / Math.log(2));
		// t = m - (s + b)
		numTagBits = 8 - (numSetBits + numBlockBits);
		//cache will have S sets, with E lines per set. S = C/(BE), E = E
		cache = new String[cacheSize/(dataBlockSize*assoc)][assoc];
		cacheUse = new int[cacheSize/(dataBlockSize*assoc)][assoc];
		//set all values in cache to empty string
		for(int i = 0; i < cache.length; i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				cache[i][j] = "";
			}
		}
		//set all values in cache to a string of 0s, length of each line being 2 (1 for valid 1 for dirty) + B*2 (2 for each block of data) + t (one for each tag bit)
		for(int i = 0; i < cache.length; i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				for(int k = 0; k < (dataBlockSize*2 + 2 + numTagBits); k++)
				{
					cache[i][j] = cache[i][j] + "0";
				}
			}
		}
		
		String curCommand = "";
		curCommand = userInput.nextLine();
		while(true)
		{
			System.out.println("*** Cache simulator menu ***");
			System.out.println("type one command:");
			System.out.println("1. cache-read\n2. cache-write\n3. cache-flush\n4. cache-view\n5. memory-view\n6. cache-dump\n7. memory-dump\n8. quit");
			System.out.println("****************************");
			curCommand = userInput.nextLine();
			
			if(curCommand.indexOf("cache-read") != -1)
				readCache(curCommand);
			else if(curCommand.indexOf("cache-write") != -1)
				writeCache(curCommand);
			else if(curCommand.equals("cache-flush"))
				flushCache();
			else if(curCommand.equals("cache-view"))
				viewCache();
			else if(curCommand.equals("memory-view"))
				viewMemory();
			else if(curCommand.equals("cache-dump"))
				dumpCache();
			else if(curCommand.equals("memory-dump"))
				dumpMemory();
			else if(curCommand.equals("quit"))
				break;
			else
			{
				//placeholder...
				int j = 0;
			}
		}
	}
	
	/**@brief
	*This function uses the command given in simulateCache() to attempt to read from the cache. If a line within the set dictated by the address has a valid bit set to 1 and the tag matches, then the read was a success (hit), and the LRU status is updated.
	*
	*Otherwise, the read failed (miss), and the data dictated by the address is loaded into the first empty line within the set, or if all lines are full, evicts one and replaces its contents with the data dictated by address according to the replacement policy.
	*@param chosenAddress
	*The address to read from, given by the user.
	*/
	public static void readCache(String chosenAddress) throws FileNotFoundException
	{
		//input string must be length 15, cache-read = 10 characters, 1 space, and 4 for the hex address (0xXX)
		while(chosenAddress.length() != 15)
		{
			System.out.println("invalid address. try again.");
			chosenAddress = userInput.nextLine();
		}
		//trim to just the address part (0xXX)
		chosenAddress = chosenAddress.substring(11);
		
		//convert address to a binary string for parsing, to find block offset, tag, etc.
		String address = Integer.toBinaryString(Integer.parseInt(chosenAddress.substring(2), 16));
		while(address.length() < 8)
		{
			address = "0" + address;
		}
		
		//tag bits are at the beginning of the address
		String tag = address.substring(0, numTagBits);
		int set = 0;
		//would be an error if there were no set bits, so make sure there are supposed to be set bits before calculating
		if(numSetBits != 0)
			set = Integer.parseInt(address.substring(numTagBits, numTagBits+numSetBits), 2);
		//block offset bits are at the end of the address
		int blockOffset = Integer.parseInt(address.substring(address.length()-numBlockBits), 2);
		
		String data = "";
		boolean hit = false;
		int evictionLine = -1;
		for(int i = 0; i < cache[set].length; i++)
		{
			String curString = cache[set][i];
			
			//set eviction line to the first empty line within that cache set. will be used in the case of a cache miss
			if(evictionLine == -1 && isAll0s(curString))
				evictionLine = i;
			else if(!hit && curString.substring(0, 1).equals("1") && curString.substring(2, 2+numTagBits).equals(tag))
			{
				//hit, so read the data from the cache and update LRU
				data = "0x" + curString.substring(2+numTagBits+(blockOffset*2), 4+numTagBits+(blockOffset*2));
				cacheUse[set][i] = 0;
				for(int j = 0; j < cacheUse[set].length; j++)
				{
					if(i != j)
						cacheUse[set][j] += 1;
				}
				hit = true;
			}
		}
		
		System.out.println("set:"+set);
		System.out.println("tag:"+tag);
		if(!hit)
		{
			//lines 282-302: determine what to read from RAM, such that the entire block is full within the cache, and the information all comes from the same block of 8 bytes in RAM. try to go forward first,
			//if fails, backtrack to get extra numbers
			int RAMPos = Integer.parseInt(address, 2);
			data = Integer.toHexString((int)RAM[RAMPos]);
			while(data.length() < 2)
				data = "0" + data;
			data = data.toUpperCase();
			data = "0x"+data;
			int endPos = RAMPos;
			
			while((endPos+1)%8 != 0)
			{
				if(endPos - RAMPos + 1 == dataBlockSize)
					break;
				endPos++;
			}
			
			int nums = endPos - RAMPos + 1;
			while(nums != dataBlockSize)
			{
				nums++;
				RAMPos--;
			}
			
			//get the blocks from the RAM
			String dataToLoad = "";
			for(int i = RAMPos; i <= endPos; i++)
			{
				if(i < RAM.length)
				{
					String toAdd = Integer.toHexString((int)RAM[i]);
					while(toAdd.length() < 2)
						toAdd = "0" + toAdd;
					dataToLoad = dataToLoad + toAdd;
				}
				else
					System.out.println("uhh.... tried to access out of ram range in read function");
				dataToLoad = dataToLoad.toUpperCase();
			}
			
			//if there was an empty line within the cache set
			if(evictionLine != -1)
			{
				//load data from RAM to cache, update LRU
				cache[set][evictionLine] = "10" + tag + dataToLoad;
				cacheUse[set][evictionLine] = 0;
				for(int j = 0; j < cacheUse[set].length; j++)
				{
					if(evictionLine != j)
						cacheUse[set][j] += 1;
				}
			}
			else
			{
				if(repPolicy == 1)
				{
					//random replacement. choose a random line within the set and replace its contents with those of the address given. update LRU
					evictionLine = (int)(Math.random() * (cache[set].length-1));
					cache[set][evictionLine] = "10" + tag + dataToLoad;
					cacheUse[set][evictionLine] = 0;
					for(int j = 0; j < cacheUse[set].length; j++)
					{
						if(evictionLine != j)
							cacheUse[set][j] += 1;
					}
				}
				else
				{
					//LRU. evict the first line with the HIGHEST usage number; corresponds to the one that was used last.
					evictionLine = 0;
					for(int i = 1; i < cacheUse[set].length; i++)
					{
						if(cacheUse[set][i] > cacheUse[set][evictionLine])
							evictionLine = i;
					}
					
					cache[set][evictionLine] = "10" + tag + dataToLoad;
					cacheUse[set][evictionLine] = 0;
					for(int j = 0; j < cacheUse[set].length; j++)
					{
						if(evictionLine != j)
							cacheUse[set][j] += 1;
					}
				}
			}
			
			System.out.println("hit:no");
			System.out.println("eviction_line:"+evictionLine);
			System.out.println("ram_address:"+chosenAddress);
			System.out.println("data:"+data);
		}
		else
		{
			System.out.println("hit:yes");
			System.out.println("eviction_line:-1");
			System.out.println("ram_address:-1");
			System.out.println("data:"+data);
		}
		
		if(hit)
			numCacheHits++;
		else
			numCacheMisses++;
	}
	
	/**@brief
	*This function uses the command given in simulateCache() to attempt to write to the cache. If a line within the set dictated by the address has a valid bit set to 1 and the tag matches, then the write was a success (hit), and the content of the corresponding
	*cache line is updated with the data given in the argument; dirty bit also gets set to 1. The data is also written to RAM in the case of a write-through policy.
	*
	*Otherwise, the write failed, and the data is written only in the RAM (in the case of a no write-allocate policy), or is loaded from the RAM and written in the cache (write-allocate).
	*@param dataAndAddress
	*A String containing both the address to write to, and the data to write at that address. Given by the user.
	*/
	public static void writeCache(String dataAndAddress) throws FileNotFoundException
	{
		//length 21. example: cache-write 0x00 0xff -> 8 for address/data + 2 spaces + 11 for "cache-write"
		while(dataAndAddress.length() != 21)
		{
			System.out.println("invalid data or address. try again.");
			dataAndAddress = userInput.nextLine();
		}
		//only retrieve the part starting at 0xXX
		dataAndAddress = dataAndAddress.substring(12);
		dataAndAddress = dataAndAddress.toUpperCase();
		
		//data is the second operand
		String data = dataAndAddress.substring(7);
		//convert the first operand, the address, into a binary string for parsing for tag, set, block offset
		String address = Integer.toBinaryString(Integer.parseInt(dataAndAddress.substring(2, 4), 16));
		while(address.length() < 8)
		{
			address = "0" + address;
		}
		
		String tag = address.substring(0, numTagBits);
		int set = 0;
		if(numSetBits != 0)
			set = Integer.parseInt(address.substring(numTagBits, numTagBits+numSetBits), 2);
		int blockOffset = Integer.parseInt(address.substring(address.length()-numBlockBits), 2);
		
		boolean hit = false;
		int evictionLine = -1;
		int dirtyBit = 0;
		for(int i = 0; i < cache[set].length; i++)
		{
			String curString = cache[set][i];
			
			if(evictionLine == -1 && isAll0s(curString))
				evictionLine = i;
			else if(!hit && curString.substring(0, 1).equals("1") && curString.substring(2, 2+numTagBits).equals(tag))
			{
				//hit. check if the existing data in RAM matches what is about to be loaded into the cache. if it does, then dirty bit = 0; else, = 1.
				int RAMPos = Integer.parseInt(address, 2);
				int endPos = RAMPos;
				
				while((endPos+1)%8 != 0)
				{
					if(endPos - RAMPos + 1 == dataBlockSize)
						break;
					endPos++;
				}
				
				int nums = endPos - RAMPos + 1;
				while(nums != dataBlockSize)
				{
					nums++;
					RAMPos--;
				}
				
				String dataToLoad = "";
				for(int j = RAMPos; j <= endPos; j++)
				{
					if(j < RAM.length)
					{
						String toAdd = Integer.toHexString((int)RAM[j]);
						while(toAdd.length() < 2)
							toAdd = "0" + toAdd;
						
						dataToLoad = dataToLoad + toAdd;
					}
					else
						System.out.println("uhh.... tried to access out of ram range in write function");
					dataToLoad = dataToLoad.toUpperCase();
				}
				String temp = cache[set][i].substring(2+numTagBits, 2+numTagBits+(blockOffset*2)) + data + cache[set][i].substring(4+numTagBits+(blockOffset*2));
				temp = temp.toUpperCase();
				if(!dataToLoad.equals(temp))
					dirtyBit = 1;
				//preserve the valid, dirty, and tag bits and all blocks before the one to replace; add the replaced block; add the blocks after the replaced block's position. update LRU
				cache[set][i] = "1" + dirtyBit + tag + cache[set][i].substring(2+numTagBits, 2+numTagBits+(blockOffset*2)) + data + cache[set][i].substring(4+numTagBits+(blockOffset*2));
				cacheUse[set][i] = 0;
				for(int j = 0; j < cacheUse[set].length; j++)
				{
					if(i != j)
						cacheUse[set][j] += 1;
				}
				hit = true;
			}
		}
		
		//if write-through, also write to RAM
		if(writeHPolicy == 1)
		{
			RAM[Integer.parseInt(address, 2)] = Integer.parseInt(data, 16);
		}
		System.out.println("set:"+set);
		System.out.println("tag:"+tag);
		if(!hit)
		{
			//no write-allocate: only write to RAM
			if(writeMPolicy == 2)
			{
				RAM[Integer.parseInt(address, 2)] = Integer.parseInt(data, 16);
			}
			//write-allocate: load from RAM, write to cache
			else
			{
				//dirtyBit = 0//writeHPolicy == 1 ? 0 : 1; (discard)
				int RAMPos = Integer.parseInt(address, 2);
				int endPos = RAMPos;
				
				while((endPos+1)%8 != 0)
				{
					if(endPos - RAMPos + 1 == dataBlockSize)
						break;
					endPos++;
				}
				
				int nums = endPos - RAMPos + 1;
				while(nums != dataBlockSize)
				{
					nums++;
					RAMPos--;
				}
				
				String dataToLoad = "";
				for(int i = RAMPos; i <= endPos; i++)
				{
					if(i < RAM.length)
					{
						String toAdd = Integer.toHexString((int)RAM[i]);
						while(toAdd.length() < 2)
							toAdd = "0" + toAdd;
						
						dataToLoad = dataToLoad + toAdd;
					}
					else
						System.out.println("uhh.... tried to access out of ram range in read function");
					dataToLoad = dataToLoad.toUpperCase();
				}
				
				//if there was an empty line within the set, use that
				if(evictionLine != -1)
				{	
					//first, set cache data to what came from RAM
					//second, replace the block specified by the address with the given data
					cache[set][evictionLine] = "1" + dirtyBit + tag + dataToLoad;
					cache[set][evictionLine] = "1" + dirtyBit + tag + cache[set][evictionLine].substring(2+numTagBits, 2+numTagBits+(blockOffset*2)) + data + cache[set][evictionLine].substring(4+numTagBits+(blockOffset*2));
					//update LRU
					cacheUse[set][evictionLine] = 0;
					for(int j = 0; j < cacheUse[set].length; j++)
					{
						if(evictionLine != j)
							cacheUse[set][j] += 1;
					}
				}
				//else, consult eviction policy
				else
				{
					if(repPolicy == 1)
					{
						//random eviction. update LRU.
						evictionLine = (int)(Math.random() * (cache[set].length-1));
						cache[set][evictionLine] = "1" + dirtyBit + tag + dataToLoad;
						cache[set][evictionLine] = "1" + dirtyBit + tag + cache[set][evictionLine].substring(2+numTagBits, 2+numTagBits+(blockOffset*2)) + data + cache[set][evictionLine].substring(4+numTagBits+(blockOffset*2));
						cacheUse[set][evictionLine] = 0;
						for(int j = 0; j < cacheUse[set].length; j++)
						{
							if(evictionLine != j)
								cacheUse[set][j] += 1;
						}
					}
					else
					{
						//LRU eviction. update LRU.
						evictionLine = 0;
						for(int i = 1; i < cacheUse[set].length; i++)
						{
							if(cacheUse[set][i] > cacheUse[set][evictionLine])
								evictionLine = i;
						}
						
						cache[set][evictionLine] = "1" + dirtyBit + tag + dataToLoad;
						cache[set][evictionLine] = "1" + dirtyBit + cache[set][evictionLine].substring(2+numTagBits, 2+numTagBits+(blockOffset*2)) + data + cache[set][evictionLine].substring(4+numTagBits+(blockOffset*2));
						cacheUse[set][evictionLine] = 0;
						for(int j = 0; j < cacheUse[set].length; j++)
						{
							if(evictionLine != j)
								cacheUse[set][j] += 1;
						}
					}
				}
			}
			System.out.println("write_hit:no");
			System.out.println("eviction_line:"+evictionLine);
			System.out.println("ram_address:0x"+dataAndAddress.substring(2, 4));
			System.out.println("data:0x"+dataAndAddress.substring(7));
			System.out.println("dirty_bit:"+dirtyBit);
		}
		else
		{
			System.out.println("write_hit:yes");
			System.out.println("eviction_line:-1");
			System.out.println("ram_address:-1");
			System.out.println("data:0x"+dataAndAddress.substring(7));
			System.out.println("dirty_bit:"+dirtyBit);
		}
		
		if(hit)
			numCacheHits++;
		else
			numCacheMisses++;
	}
	
	/**@brief
	*This function resets the cache to its initial state (all values set to 0). It also resets all of the cache use rates, relevant in the case of a LRU policy.
	*/
	public static void flushCache()
	{
		//sets cache to all 0s the same as in simulateCache()
		cache = new String[cacheSize/(dataBlockSize*assoc)][assoc];
		cacheUse = new int[cacheSize/(dataBlockSize*assoc)][assoc];
		
		for(int i = 0; i < cache.length; i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				for(int k = 0; k < (dataBlockSize*2 + 2 + numTagBits); k++)
				{
					if(k == 0)
						cache[i][j] = "0";
					else
						cache[i][j] = cache[i][j] + "0";
				}
			}
		}
		
		System.out.println("cache_cleared");
	}
	
	/**@brief
	*This function displays some information about the cache as well as all of the cache contents to the user. It displays the size of the cache, the data block size, the associativity, the replacement policy, the write hit policy,
	*the write miss policy, the total number of cache hits, the total number of cache misses, and finally, the content of the cache. The first two entries in the cache are the valid and dirty bits; the next value is a hex value representing the tag,
	*and everything after that is the data within that cache line.
	*/
	public static void viewCache()
	{
		System.out.println("cache_size:"+cacheSize);
		System.out.println("data_block_size:"+dataBlockSize);
		System.out.println("associativity:"+assoc);
		
		if(repPolicy == 1)
			System.out.println("replacement_policy:random_replacement");
		else
			System.out.println("replacement_policy:least_recently_used_replacement");
		
		if(writeHPolicy == 1)
			System.out.println("write_hit_policy:write_through");
		else
			System.out.println("write_hit_policy:write_back");
		
		if(writeMPolicy == 1)
			System.out.println("write_miss_policy:write_allocate");
		else
			System.out.println("write_miss_policy:no_write_allocate");
		
		System.out.println("number_of_cache_hits:"+numCacheHits);
		System.out.println("number_of_cache_misses:"+numCacheMisses);
		System.out.println("cache_content:");
		
		for(int i = 0; i < cache.length; i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				//convert tag, represented in binary in my cache, to a hexadecimal string. pad with 0s if necessary.
				String tag = Integer.toHexString((Integer.parseInt((cache[i][j].substring(2, 2+numTagBits)), 2)));
				while(tag.length() < 2)
					tag = "0" + tag;
				
				//initialize string to output with the valid, dirty, and tag bits, separated by spaces
				String toOutput = cache[i][j].substring(0, 1) + " " + cache[i][j].substring(1, 2) + " " + tag + " ";
				//print each block one after another. two characters per block, separated by a space.
				int blocksPrinted = 0;
				while(blocksPrinted < dataBlockSize)
				{
					toOutput = toOutput + cache[i][j].substring(2+numTagBits+(blocksPrinted*2), 4+numTagBits+(blocksPrinted*2)) + " ";
					blocksPrinted++;
				}
				toOutput = toOutput.toUpperCase();
				
				System.out.println(toOutput);
			}
		}
	}
	
	/**@brief
	*This function displays some information about the memory as well as the addresses in memory and corresponding data within those addresses. It displays the memory size (bytes) and the content of the memory.
	*/
	public static void viewMemory()
	{
		System.out.println("memory_size:"+RAM.length);
		System.out.println("memory_content:");
		System.out.println("address:data");
		
		for(int i = 0; i < RAM.length; i++)
		{
			if(i%8 == 0)
			{
				//print newline for every 8 positions in ram
				if(i != 0)
					System.out.println();
				
				String toAdd = Integer.toHexString(i);
				while(toAdd.length() < 2)
					toAdd = "0" + toAdd;
				toAdd = toAdd.toUpperCase();
				//print address at the beginning of each newline
				System.out.print("0x"+toAdd+":");
			}
			
			//convert integer data in RAM to hex string. pad with 0s if necessary. also make uppercase
			String toPrint = Integer.toHexString((int)RAM[i]);
			while(toPrint.length() < 2)
				toPrint = "0" + toPrint;
			toPrint = toPrint.toUpperCase();
			
			System.out.print(toPrint);
			//if more RAM will be read, print a space.
			if((i+1)%8 != 0)
				System.out.print(" ");
		}
		
		System.out.print("\n");
	}
	
	/**@brief
	*This function takes the current cache and loads it into an output file named "cache.txt". Each line corresponds to one line in the cache, and each line shows only the data within the cache line, not the tag or valid/dirty bits.
	*/
	public static void dumpCache() throws IOException
	{
		FileWriter myWriter = new FileWriter("cache.txt");
		for(int i = 0; i < cache.length; i++)
		{
			for(int j = 0; j < cache[i].length; j++)
			{
				int blocksPrinted = 0;
				while(blocksPrinted < dataBlockSize)
				{
					//retrieve block by block from the cache line. skip the valid/dirty/tag bits. make uppercase.
					String toWrite = cache[i][j].substring(2+numTagBits+(blocksPrinted*2), 4+numTagBits+(blocksPrinted*2));
					toWrite = toWrite.toUpperCase();
					myWriter.write(toWrite);
					System.out.print(toWrite);
					
					//print a space if more to be read
					if((++blocksPrinted) < dataBlockSize)
					{
						myWriter.write(" ");
						System.out.print(" ");
					}
				}
				
				//if not the final line of cache, print a new line for the next line of data
				if(i != cache.length - 1 || j != cache[i].length - 1)
				{
					System.out.println();
					myWriter.write(System.getProperty( "line.separator" ));
				}
			}
		}
		System.out.println();
		myWriter.close();
	}
	
	/**@brief
	*This function takes the current state of the memory and dumps it into an output file "ram.txt". Each line corresponds to the data in sequential positions of the memory.
	*/
	public static void dumpMemory() throws IOException
	{
		FileWriter myWriter = new FileWriter("ram.txt");
		for(int i = 0; i < RAM.length; i++)
		{	
			//convert integer data in RAM to hexadecimal string. pad with 0s if necessary, make uppercase
			String toWrite = Integer.toHexString((int)RAM[i]);
			while(toWrite.length() < 2)
				toWrite = "0" + toWrite;
			toWrite = toWrite.toUpperCase();
			
			myWriter.write(toWrite);
			System.out.print(toWrite);
			
			//newline if not at the end of the RAM
			if(i != RAM.length - 1)
			{
				System.out.println();
				myWriter.write(System.getProperty( "line.separator" ));
			}
		}
		myWriter.close();
		
		System.out.println();
	}
	
	/**@brief
	*This function is a helper function that determines if the specified line of the cache is "cold".
	*@param str
	*The line of the cache to check.
	*/
	public static boolean isAll0s(String str)
	{
		boolean all0s = true;
		//checks if the entire line of the cache is 0s. if it is, then it's cold.
		for(int i = 0; i < (2*dataBlockSize + numTagBits + 2); i++)
		{
			if(!str.substring(i, i+1).equals("0"))
				all0s = false;
		}
		return all0s;
	}
}