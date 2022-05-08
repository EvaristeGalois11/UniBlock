package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.helper.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Block {
    private final BlockHeader blockHeader;
    private final List<Event> events;

    public Block(String previousHash, int difficulty) {
        blockHeader = new BlockHeader(previousHash, difficulty);
        events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        addEvents(Collections.singletonList(event));
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
        blockHeader.setRootHash(MerkleTree.rootHash(this.events));
    }

    @Override
    public String toString() {
        return StringHelper.formatTitle("Block Header") + "\n"
                + blockHeader + "\n"
                + StringHelper.formatTitle("Events") + "\n"
                + eventsToString() + "\n"
                + StringHelper.emptyLine();
    }

    private String eventsToString() {
        return events.stream().map(Object::toString).collect(Collectors.joining("\n" + StringHelper.emptyLine() + "\n"));
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public List<Event> getEvents() {
        return events;
    }
}
