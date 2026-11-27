package fr.gherriy.relais.api.cleapi;

public record CleGeneree(String valeur, String prefixe, String hash) {

    @Override
    public String toString() {
        return "CleGeneree[prefixe=" + prefixe + "]";
    }
}
