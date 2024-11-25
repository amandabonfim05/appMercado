package com.example.paginainicial;

public class Trilha {
    private int id;
    private long startDate;
    private double velocidadeMedia;
    private double distancia;
    private long duracao;

    public Trilha(int id, long startDate, double averageSpeed, double distance, long duration) {
        this.id = id;
        this.startDate = startDate;
        this.velocidadeMedia = averageSpeed;
        this.distancia = distance;
        this.duracao = duration;
    }

    // Getters
    public int getId() {
        return id;
    }

    public long getStartDate() {
        return startDate;
    }

    public double getVelocidadeMedia() {
        return velocidadeMedia;
    }

    public double getDistancia() {
        return distancia;
    }

    public long getDuracao() {
        return duracao;
    }
}
