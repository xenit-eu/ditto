package eu.xenit.testing.ditto.api;

import static org.assertj.core.api.Assertions.assertThat;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;

import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.internal.DefaultDataSetBuilder;
import eu.xenit.testing.ditto.internal.DefaultDataSetBuilderFactory;
import org.junit.jupiter.api.Test;

class AlfrescoBootstrapperTest {

    @Test
    void testCompanyHome() {

        DefaultDataSetBuilder builder = new AlfrescoBootstrapper<DefaultDataSetBuilder>().bootstrap(builder());

        assertThat(builder).hasTransactionWithId(6L, txn -> {
            txn.hasNodeWithId(13L, node -> node
                            .hasName("Company Home")
                            .hasType(Content.FOLDER)
                            .hasQNamePath("/app:company_home")
                            .withAspects(aspects -> assertThat(aspects).containsExactlyInAnyOrder(
                                    Content.TITLED,
                                    Content.AUDITABLE,
                                    System.REFERENCEABLE,
                                    System.LOCALIZED,
                                    Application.UIFACETS
                            ))
                            .hasParent(parent -> parent.hasType(ContentModel.System.STORE_ROOT))
            );
        });
    }

    private static DefaultDataSetBuilder builder() {
        return new DefaultDataSetBuilderFactory().createBuilder(BootstrapConfiguration.withDefaults());
    }

}