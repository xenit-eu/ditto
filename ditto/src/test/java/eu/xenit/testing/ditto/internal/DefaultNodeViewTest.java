package eu.xenit.testing.ditto.internal;

import static eu.xenit.testing.ditto.internal.DittoAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import org.junit.jupiter.api.Test;

class DefaultNodeViewTest {

    @Test
    void testCompanyHome_present() {
        NodeView nodeView = AlfrescoDataSet.bootstrapAlfresco().build().getNodeView();

        assertThat(nodeView.getCompanyHome())
                .hasValueSatisfying(companyHome -> assertThat(companyHome)
                        .hasNodeId(13)
                        .hasType(Content.FOLDER)
                );
    }

    @Test
    void testCompanyHome_emptyOptional() {
        NodeView nodeView = AlfrescoDataSet.empty().getNodeView();

        assertThat(nodeView.getCompanyHome()).isNotPresent();
    }

}