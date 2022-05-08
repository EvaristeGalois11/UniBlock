package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.helper.crypto.HashHelper;

import java.util.List;

public class MerkleTree {
    public static String rootHash(List<Event> events) {
        return getRootHash(events.stream().map(HashHelper::hash).toList());
    }

    private static String getRootHash(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes.get(0);
        } else {
            return null;
//            return getRootHash(Lists.partition(hashes, 2).stream().map(MerkleTree::reduceHash).toList());
        }
    }

    private static String reduceHash(List<String> hashes) {
        var first = hashes.get(0);
        var second = hashes.size() == 2 ? hashes.get(1) : first;
        return HashHelper.hash(first + second);
    }
}
