package eu.xenit.testing.ditto.api;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.data.ContentModel;
import eu.xenit.testing.ditto.api.data.ContentModel.Application;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.Version2;
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

    private static final String FIXED_UUID_PROJECT_CONTRACT = "1a0b110f-1e09-4ca2-b367-fe25e4964a4e";

    @Test
    void testVersionedNode() {

        DefaultDataSetBuilder builder = new AlfrescoBootstrapper<DefaultDataSetBuilder>().bootstrap(builder());

        assertThat(builder).hasTransactionWithId(10L, txn -> {
            txn.hasNodeWithId(605L, node -> node
                    .hasUuid(FIXED_UUID_PROJECT_CONTRACT)
                    .hasName("Project Contract.pdf")
                    .hasType(Content.CONTENT)
                    .hasQNamePath(
                            "/app:company_home/st:sites/cm:swsdp/cm:documentLibrary/cm:Agency_x0020_Files/cm:Contracts/cm:Project_x0020_Contract.pdf")
                    .withAspects(aspects -> assertThat(aspects).containsExactlyInAnyOrder(
                            Content.TITLED,
                            Content.AUDITABLE,
                            Content.VERSIONABLE,
                            System.REFERENCEABLE,
                            System.LOCALIZED
                    ))
                    .hasParent(parent -> parent.hasName("Contracts"))
            );
            txn.hasNodeWithId(606L, node -> node
                    .hasType(Version2.VERSION_HISTORY)
                    .hasProperty(Version2.VERSIONED_NODE_ID, FIXED_UUID_PROJECT_CONTRACT)
                    .hasParent(parent -> parent.hasType(System.STORE_ROOT)));
            txn.hasNodeWithId(607L, node -> node
                    .hasName("Project Contract.pdf")
                    .hasType(Content.CONTENT)
                    .hasProperty(Version2.FROZEN_NODE_REF, "workspace://SpacesStore/" + FIXED_UUID_PROJECT_CONTRACT)
                    .hasProperty(Version2.FROZEN_CREATED)
                    .hasProperty(Version2.FROZEN_CREATOR)
                    .hasProperty(Version2.FROZEN_MODIFIED)
                    .hasProperty(Version2.FROZEN_MODIFIER)
                    .hasProperty(Version2.VERSION_LABEL, "1.1")
                    .hasProperty(Version2.VERSION_DESCRIPTION, null)
                    .withAspects(aspects -> assertThat(aspects).containsExactlyInAnyOrder(
                            Content.TITLED,
                            Content.AUDITABLE,
                            Content.VERSIONABLE,
                            System.REFERENCEABLE,
                            System.LOCALIZED
                    ))
                    .hasParent(parent -> parent.hasNodeId(606L)));
        });
    }

    private static DefaultDataSetBuilder builder() {
        return new DefaultDataSetBuilderFactory().createBuilder(BootstrapConfiguration.withDefaults());
    }

}