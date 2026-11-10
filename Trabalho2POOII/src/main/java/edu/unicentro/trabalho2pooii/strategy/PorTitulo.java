package edu.unicentro.trabalho2pooii.strategy;

import edu.unicentro.trabalho2pooii.model.Nota;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Concrete Strategy: ordem alfabética pelo título, sem diferenciar maiúsculas de minúsculas.
public class PorTitulo implements EstrategiaOrdenacao {

    @Override
    public String nome() {
        return "Título (A-Z)";
    }

    @Override
    public List<Nota> ordenar(List<Nota> notas) {
        List<Nota> copia = new ArrayList<>(notas);
        copia.sort(Comparator.comparing(Nota::getNome, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Nota::getNotaID));
        return copia;
    }
}
