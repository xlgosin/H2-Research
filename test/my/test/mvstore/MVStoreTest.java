package my.test.mvstore;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

public class MVStoreTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MVStore.Builder builder = new MVStore.Builder();
		builder.cacheSize(10).compressData().readOnly().writeDelay(2000);

		String str = builder.toString();

		System.out.println(str);
		builder = MVStore.Builder.fromString(str);

		// open the store (in-memory if fileName is null)
		String fileName = null;
		fileName = "E:/H2/baseDir/MVStoreTest333";
		MVStore store = MVStore.open(fileName);

		// create/get the map named "data"
		MVMap<Integer, String> map = store.openMap("data");

		// add some data
		map.put(1, "Hello");
		map.put(2, "World");

		// get the current version, for later use
		long oldVersion = store.getCurrentVersion();

		// from now on, the old version is read-only
		store.incrementVersion();

		// more changes, in the new version
		// changes can be rolled back if required
		// changes always go into "head" (the newest version)
		map.put(1, "Hi");
		map.remove(2);

		// access the old data (before incrementVersion)
		MVMap<Integer, String> oldMap = map.openVersion(oldVersion);

		// mark the changes as committed
		store.commit();

		// print the old version (can be done
		// concurrently with further modifications)
		// this will print "Hello" and "World":
		System.out.println(oldMap.get(1));
		System.out.println(oldMap.get(2));
		oldMap.close();

		// print the newest version ("Hi")
		System.out.println(map.get(1));

		System.out.println(map.get(2));

		// close the store - this doesn't write to disk
		store.close();
	}

}
