package edu.unicentro.trabalho2pooii.model;

public class Usuario {

    private int userID;
    private String nome;
    private String senha;

    public Usuario() {
    }

    public Usuario(int userID, String nome, String senha) {
        this.userID = userID;
        this.nome = nome;
        this.senha = senha;
    }

    // Usado ao cadastrar: o ID ainda não existe, o banco é quem gera.
    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // A senha fica de fora de propósito, para não aparecer em prints e logs.
    @Override
    public String toString() {
        return "Usuario{" +
                "userID=" + userID +
                ", nome='" + nome + '\'' +
                '}';
    }
}
