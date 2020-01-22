package eu.xenit.testing.ditto.api.data;

public interface ContentModel {

    interface Content {

        String FOLDER = "{http://www.alfresco.org/model/content/1.0}folder";
        String CONTENT = "{http://www.alfresco.org/model/content/1.0}content";

        String AUDITABLE = "{http://www.alfresco.org/model/content/1.0}auditable";

        String NAME = "{http://www.alfresco.org/model/content/1.0}name";

        String CREATOR = "{http://www.alfresco.org/model/content/1.0}creator";
        String CREATED = "{http://www.alfresco.org/model/content/1.0}created";
        String MODIFIER = "{http://www.alfresco.org/model/content/1.0}modifier";
        String MODIFIED = "{http://www.alfresco.org/model/content/1.0}modified";

    }

    interface System {

        String STORE_ROOT = "{http://www.alfresco.org/model/system/1.0}store_root";

        String NODE_DBID = "{http://www.alfresco.org/model/system/1.0}node-dbid";

        String STORE_PROTOCOL = "{http://www.alfresco.org/model/system/1.0}store-protocol";
        String STORE_IDENTIFIER = "{http://www.alfresco.org/model/system/1.0}store-identifier";
        String NODE_UUID = "{http://www.alfresco.org/model/system/1.0}node-uuid";

        String LOCALE = "{http://www.alfresco.org/model/system/1.0}locale";

    }

}

