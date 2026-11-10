package edu.unicentro.trabalho2pooii.strategy;

import edu.unicentro.trabalho2pooii.model.Nota;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Concrete Strategy: reproduz a ordem original (antes ficava fixa no SQL):
// notas fixadas no topo e, dentro de cada grupo, por ordem de criação.
public class FixadasPrimeiro implements EstrategiaOrdenacao {

    @Override
    public String nome() {
        return "Fixadas primeiro";
    }

    @Override
    public List<Nota> ordenar(List<Nota> notas) {
        Comparator<Nota> fixadasNoTopo = (a, b) -> Boolean.compare(b.isFixado(), a.isFixado());
        List<Nota> copia = new ArrayList<>(notas);
        copia.sort(fixadasNoTopo.thenComparingInt(Nota::getNotaID));
        return copia;
    }
}
