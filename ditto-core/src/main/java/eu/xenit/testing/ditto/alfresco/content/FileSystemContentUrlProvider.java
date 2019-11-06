package eu.xenit.testing.ditto.alfresco.content;

import eu.xenit.testing.ditto.alfresco.ContentUrlProvider;
import eu.xenit.testing.ditto.alfresco.Node;
import eu.xenit.testing.ditto.alfresco.Node.NodeContext;
import eu.xenit.testing.ditto.util.StringUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class FileSystemContentUrlProvider implements ContentUrlProvider {

    private final String bucket;

    public FileSystemContentUrlProvider()
    {
        this(null);
    }

    public FileSystemContentUrlProvider(String bucket)
    {
        this.bucket = bucket;
    }


    public String createContentData(Node node, NodeContext context) {

        LocalDateTime dateTime = LocalDateTime.ofInstant(context.getInstant(), ZoneOffset.UTC);

        StringBuilder contentUrl = new StringBuilder("store://")
                .append(dateTime.getYear()).append("/")
                .append(dateTime.getMonthValue()).append("/")
                .append(dateTime.getDayOfMonth()).append("/")
                .append(dateTime.getHour()).append("/")
                .append(dateTime.getMinute()).append("/");

        if (StringUtils.hasText(bucket))
        {
            contentUrl.append(bucket).append("/");
        }

        contentUrl.append(UUID.randomUUID().toString()).append(".bin");

        return contentUrl.toString();
    }
}
