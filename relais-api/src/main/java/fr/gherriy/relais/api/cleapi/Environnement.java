package fr.gherriy.relais.api.cleapi;

public enum Environnement {
    LIVE("rl_live_"),
    TEST("rl_test_");
    private final String prefixe;

    Environnement(String prefixe) {
        this.prefixe = prefixe;
    }

    public String prefixe() {
        return prefixe;
    }
    
}
