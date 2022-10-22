package practicas.bolqueII.tftp.tools;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;


/**
 * Lista auxiliar para la implementación del protocolo de envío Repetición Selectiva
 */
public class RecivedCheckList {
    private int numBlocksRecived;
    private final SortedSet<Integer> ackList;
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

    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
