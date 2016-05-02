package org.lionsoul.jcseg.server;

import org.lionsoul.jcseg.server.controller.KeyphraseController;
import org.lionsoul.jcseg.server.controller.KeywordsController;
import org.lionsoul.jcseg.server.controller.MainController;
import org.lionsoul.jcseg.server.controller.SentenceController;
import org.lionsoul.jcseg.server.controller.SummaryController;
import org.lionsoul.jcseg.server.core.AbstractRouter;
import org.lionsoul.jcseg.server.core.ContextRouter;
import org.lionsoul.jcseg.server.core.UriEntry;

public class ContextRouterTest {

    public static void main(String[] args) {
        
         //test for contextRouter
        AbstractRouter router = new ContextRouter( MainController.class); 
        router.addMapping("/extractor/keywords", KeywordsController.class);
        router.addMapping("/extractor/keyphrase", KeyphraseController.class);
        router.addMapping("/extractor/sentence/a", SentenceController.class);
        router.addMapping("/extractor/sentence/*", SentenceController.class);
        router.addMapping("/extractor/summary/*", SummaryController.class);
        
        System.out.println(router.getController(new UriEntry("/extractor/summary/a")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/summary/a/")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/summary/a/b")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/v")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/a")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/b")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/c")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/summary/a/b")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/d")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/e")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/f")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/g")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/h")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/g")).getName());
        System.out.println(router.getController(new UriEntry("/extractor/sentence/a/b/b")).getName());
        //System.out.println(router.getController(new UriEntry("/")).getName());
        //System.out.println(router.getController(new UriEntry("")).getName());
        

    }

}
