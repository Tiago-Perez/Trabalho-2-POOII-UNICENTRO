package edu.unicentro.trabalho2pooii.model;

public class Nota {

    private int notaID;
    private int userID;
    private String nome;
    private String texto;
    private boolean fixado;

    public Nota() {
    }

    public Nota(int notaID, int userID, String nome, String texto, boolean fixado) {
        this.notaID = notaID;
        this.userID = userID;
        this.nome = nome;
        this.texto = texto;
        this.fixado = fixado;
    }

    // Usado ao cadastrar: o notaID ainda não existe, o banco é quem gera.
    public Nota(int userID, String nome, String texto) {
        this.userID = userID;
        this.nome = nome;
        this.texto = texto;

    }

    public boolean isFixado() {
        return fixado;
    }

    public void setFixado(boolean fixado) {
        this.fixado = fixado;
    }

    public int getNotaID() {
        return notaID;
    }

    public void setNotaID(int notaID) {
        this.notaID = notaID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "notaID=" + notaID +
                ", userID=" + userID +
                ", nome='" + nome + '\'' +
                ", texto='" + texto + '\'' +
                '}';
    }
}
