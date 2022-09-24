package practicas.bolqueII.tftp.tools;

import javax.swing.text.html.HTMLDocument;
import java.util.*;
import java.util.stream.IntStream;

public class RecivedCheckList {
    private int numBlocksRecived;
    private SortedSet<Integer> ackList;
    private boolean unOrderedCheckedBlock;

    public RecivedCheckList(int numBlocksRecived, SortedSet<Integer> ackList) {
        this.numBlocksRecived = numBlocksRecived;
        this.ackList = ackList;
        unOrderedCheckedBlock = false;
    }

    public SortedSet<Integer> getAckList() {
        return ackList;
    }

    public int getNumBlocksRecived() {
        return numBlocksRecived;
    }

    public boolean containsNonCheckBlocks() {
        return unOrderedCheckedBlock;
    }

    public List<Integer> getNonCheckBlocks(){
        List<Integer> result = new ArrayList<>();
        List<Integer> acks = ackList.stream().toList();

        for (int i = 0; i < ackList.size() - 1; i++) {
            if (acks.get(i) + 1 != acks.get(i + 1)) // Encontramos las dos subsecuencias
                result.addAll( IntStream.rangeClosed(acks.get(i) + 1, acks.get(i + 1) - 1)
                                         .boxed()
                                         .toList());

        }

        unOrderedCheckedBlock = !result.isEmpty();
        return result;
    }

    public void ackBlock(int blockId) throws DuplicatedBlockException {
        if (!ackList.contains(blockId)) {
            numBlocksRecived++;
            ackList.add(blockId);
            if (!ackList.contains(blockId + 1) || !ackList.contains(blockId - 1))
                unOrderedCheckedBlock = true;
        } else {
            throw new DuplicatedBlockException("Este bloque de datos ya ha sido recivido");
        }
    }
}
