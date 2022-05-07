package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.data.block.BlockJava;
import it.unifi.nave.uniblock.data.event.EventJava;
import it.unifi.nave.uniblock.persistence.PersistenceManagerJava;
import it.unifi.nave.uniblock.service.MinerServiceJava;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class MainJava {
    private static int difficulty = 5;

    public static void main(String[] args) {


    }

    private static BlockJava mineBlock(String previousHash, String message, EventJava... events) {
        var block = new BlockJava(previousHash, difficulty);
        block.addEvents(Arrays.asList(events));
        System.out.print(message);
        var start = Instant.now();
        MinerServiceJava.mine(block);
        var end = Instant.now();
        var duration = Duration.between(start, end);
        System.out.println("Block mined in " + duration.toMinutes() + "m " + duration.toSecondsPart() + "s"
                + block + "\n");
        PersistenceManagerJava.getBlockchain().saveBlock(block);
        return block;
    }

}
