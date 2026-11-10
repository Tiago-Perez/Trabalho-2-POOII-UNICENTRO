package edu.unicentro.trabalho2pooii.model;

/**
 * PADRÃO BUILDER (GoF) - papel: Builder / construtor fluente do produto Nota.
 *
 * Nota tinha dois construtores posicionais (5 e 3 parâmetros, vários do mesmo tipo
 * como int, int, String, String). Aqui cada campo é nomeado na chamada, e o
 * mesmo builder serve para criar uma nota nova, mapear uma linha do banco e
 * copiar uma nota existente (snapshot usado pelo Command para desfazer).
 */
public class NotaBuilder {

    private int notaID;
    private int userID;
    private String nome;
    private String texto = "";
    private boolean fixado;

    public NotaBuilder notaID(int notaID) {
        this.notaID = notaID;
        return this;
    }

    public NotaBuilder userID(int userID) {
        this.userID = userID;
        return this;
    }

    public NotaBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public NotaBuilder texto(String texto) {
        this.texto = texto;
        return this;
    }

    public NotaBuilder fixado(boolean fixado) {
        this.fixado = fixado;
        return this;
    }

    // Parte de uma nota existente: serve de cópia independente ou de base para editar.
    public static NotaBuilder de(Nota original) {
        return new NotaBuilder()
                .notaID(original.getNotaID())
                .userID(original.getUserID())
                .nome(original.getNome())
                .texto(original.getTexto())
                .fixado(original.isFixado());
    }

    // A validação de regra de negócio continua no NotaService.
    public Nota build() {
        Nota nota = new Nota();
        nota.setNotaID(notaID);
        nota.setUserID(userID);
        nota.setNome(nome);
        nota.setTexto(texto);
        nota.setFixado(fixado);
        return nota;
    }
}
