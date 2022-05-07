package it.unifi.nave.uniblock.data.block;

import com.google.common.collect.Lists;
import it.unifi.nave.uniblock.data.event.EventJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;

import java.util.List;

public class MerkleTreeJava {
    public static String rootHash(List<EventJava> events) {
        return getRootHash(events.stream().map(HashHelperJava::hash).toList());
    }

    private static String getRootHash(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes.get(0);
        } else {
            return getRootHash(Lists.partition(hashes, 2).stream().map(MerkleTreeJava::reduceHash).toList());
        }
    }

    private static String reduceHash(List<String> hashes) {
        if (hashes.size() == 1) {
            hashes.add(hashes.get(0));
        }
        return HashHelperJava.hash(String.join("", hashes));
    }
}
