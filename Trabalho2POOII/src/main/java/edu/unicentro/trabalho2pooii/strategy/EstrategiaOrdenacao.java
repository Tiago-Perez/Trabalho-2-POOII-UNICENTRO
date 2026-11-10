package edu.unicentro.trabalho2pooii.strategy;

import edu.unicentro.trabalho2pooii.model.Nota;
import java.util.List;

/**
 * PADRÃO STRATEGY (GoF) - papel: Strategy (interface comum dos algoritmos).
 * O Context é o NotaService, que recebe a estratégia em listar(userID, estrategia).
 * As Concrete Strategies são FixadasPrimeiro, PorTitulo e MaisRecentes.
 */
public interface EstrategiaOrdenacao {

    // Texto mostrado no menu "Ordenar por".
    String nome();

    // Devolve uma nova lista ordenada; não altera a lista recebida.
    List<Nota> ordenar(List<Nota> notas);

    // Estratégias oferecidas ao usuário. A primeira é a padrão (comportamento original).
    static List<EstrategiaOrdenacao> disponiveis() {
        return List.of(new FixadasPrimeiro(), new PorTitulo(), new MaisRecentes());
    }
}
