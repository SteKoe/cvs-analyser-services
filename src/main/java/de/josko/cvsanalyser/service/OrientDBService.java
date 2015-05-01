package de.josko.cvsanalyser.service;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public abstract class OrientDBService {
    private static final String DB = "remote:localhost/cvslogresults";

    protected OrientGraphFactory factory;
    protected OrientGraphNoTx orientGraph;

    protected void openConnection() {
        factory = new OrientGraphFactory(DB);
        orientGraph = factory.getNoTx();
    }

    protected void closeConnection() {
        orientGraph.shutdown();
        factory.close();
    }
}
