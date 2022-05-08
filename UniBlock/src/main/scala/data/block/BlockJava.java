package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.data.event.EventJava;
import it.unifi.nave.uniblock.helper.StringHelperJava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BlockJava {
    private final BlockHeaderJava blockHeader;
    private final List<EventJava> events;

    public BlockJava(String previousHash, int difficulty) {
        blockHeader = new BlockHeaderJava(previousHash, difficulty);
        events = new ArrayList<>();
    }

    public void addEvent(EventJava event) {
        addEvents(Collections.singletonList(event));
    }

    public void addEvents(List<EventJava> events) {
        this.events.addAll(events);
        blockHeader.setRootHash(MerkleTreeJava.rootHash(this.events));
    }

    @Override
    public String toString() {
        return StringHelperJava.formatTitle("Block Header") + "\n"
                + blockHeader + "\n"
                + StringHelperJava.formatTitle("Events") + "\n"
                + eventsToString() + "\n"
                + StringHelperJava.emptyLine();
    }

    private String eventsToString() {
        return events.stream().map(Object::toString).collect(Collectors.joining("\n" + StringHelperJava.emptyLine() + "\n"));
    }

    public BlockHeaderJava getBlockHeader() {
        return blockHeader;
    }

    public List<EventJava> getEvents() {
        return events;
    }
}
