package edu.unicentro.trabalho2pooii.strategy;

import edu.unicentro.trabalho2pooii.model.Nota;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Concrete Strategy: as notas criadas por último aparecem primeiro.
// O banco não guarda data de criação; o notaid é sequencial, então serve de proxy.
public class MaisRecentes implements EstrategiaOrdenacao {

    @Override
    public String nome() {
        return "Mais recentes";
    }

    @Override
    public List<Nota> ordenar(List<Nota> notas) {
        List<Nota> copia = new ArrayList<>(notas);
        copia.sort(Comparator.comparingInt(Nota::getNotaID).reversed());
        return copia;
    }
}
