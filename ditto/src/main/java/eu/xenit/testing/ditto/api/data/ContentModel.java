package eu.xenit.testing.ditto.api.data;

import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.QName;

public interface ContentModel {

    interface Content {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/content/1.0", "cm");

        QName FOLDER = createQName("folder");
        QName CONTENT = createQName("content");
        QName OBJECT = createQName("object");


        QName CONTAINS = createQName("contains");

        QName TITLED = createQName("titled");
        QName TITLE = createQName("title");
        QName DESCRIPTION = createQName("description");

        QName AUDITABLE = createQName("auditable");
        QName NAME = createQName("name");

        QName CREATOR = createQName("creator");
        QName CREATED = createQName("created");
        QName MODIFIER = createQName("modifier");
        QName MODIFIED = createQName("modified");

        QName OWNER = createQName("owner");

        QName VERSIONABLE = createQName("versionable");
        QName VERSION_LABEL = createQName("versionLabel");
        QName VERSION_TYPE = createQName("versionType");
        QName INITIAL_VERSION = createQName("initialVersion");
        QName AUTO_VERSION = createQName("autoVersion");
        QName AUTO_VERSION_ON_UPDATE_PROPS = createQName("autoVersionOnUpdateProps");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }
    }

    interface System {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/system/1.0", "sys");

        QName STORE_ROOT = createQName("store_root");
        QName CONTAINER = createQName("container");

        QName CHILDREN = createQName("children");

        QName NODE_DBID = createQName("node-dbid");

        QName REFERENCEABLE = createQName("referenceable");
        QName STORE_PROTOCOL = createQName("store-protocol");
        QName STORE_IDENTIFIER = createQName("store-identifier");
        QName NODE_UUID = createQName("node-uuid");

        QName LOCALE = createQName("locale");
        QName LOCALIZED = createQName("localized");
        QName ASPECT_ROOT = createQName("aspect_root");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }
    }

    interface User {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/user/1.0", "usr");

        QName USER = createQName("user");
        QName USERNAME = createQName("username");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }
    }

    interface Application {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/application/1.0", "app");

        QName UIFACETS = createQName("uifacets");
        QName ICON = createQName("icon");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }
    }

    interface Site {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/site/1.0", "st");
        QName SITE = createQName("site");
        QName SITE_PRESET = createQName("sitePreset");
        QName SITE_VISIBILITY = createQName("siteVisibility");

        QName SITES = createQName("sites");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }

    }

    interface Version2 {

        Namespace NAMESPACE = Namespace.createNamespace("http://www.alfresco.org/model/versionstore/2.0", "ver2");

        QName VERSION_HISTORY = createQName("versionHistory");
        QName VERSION = createQName("version");
        QName ROOT_VERSION = createQName("rootVersion");
        QName VERSIONED_NODE_ID = createQName("versionedNodeId");

        QName FROZEN_NODE_REF = createQName("frozenNodeRef");
        QName FROZEN_CREATED = createQName("frozenCreated");
        QName FROZEN_CREATOR = createQName("frozenCreator");
        QName FROZEN_MODIFIED = createQName("frozenModified");
        QName FROZEN_MODIFIER = createQName("frozenModifier");
        QName VERSION_LABEL = createQName("versionLabel");
        QName VERSION_DESCRIPTION = createQName("versionDescription");

        static QName createQName(String localName) {
            return QName.createQName(NAMESPACE, localName);
        }


    }

}

