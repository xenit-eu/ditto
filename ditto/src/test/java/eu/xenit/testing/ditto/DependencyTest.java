package eu.xenit.testing.ditto;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCycles;
import static guru.nidi.codeassert.junit.CodeAssertMatchers.matchesRulesExactly;
import static org.hamcrest.MatcherAssert.assertThat;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import guru.nidi.codeassert.dependency.DependencyResult;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import org.junit.jupiter.api.Test;

class DependencyTest {

    // Analyze all sources in src/main/java
    private final AnalyzerConfig config = AnalyzerConfig.gradle().main();

    @Test
    void noCycles() {
        assertThat(new DependencyAnalyzer(config).analyze(), hasNoCycles());
    }

    @Test
    void dependency() {

        // Note: class name for DependencyRules is significant
        class EuXenitTestingDitto extends DependencyRuler {

            // Rules for packages .ditto.api .ditto.internal and .ditto.util
            DependencyRule api, internal, util;

            @Override
            public void defineRules() {
                // .util package can be used by all
                util.mayBeUsedBy(all());

                // api may use all subpackages
                api.mayUse(api.allSubOf());
                api.sub("model").mayBeUsedBy(api.allSubOf());

                // .internal may use all subpackages
                internal.mayUse(internal.allSubOf());
                // .internal and all subpackages may use .api its subpackages
                internal.andAllSub().mayUse(api.andAllSub());
            }
        }

        // All dependencies are forbidden, except the ones defined in EuXenitTestingDitto
        // java, org, net packages may be used freely
        DependencyRules rules = DependencyRules.denyAll()
                .withRelativeRules(new EuXenitTestingDitto())
                .withExternals("java.*", "org.*", "net.*", "lombok.*");

        DependencyResult result = new DependencyAnalyzer(config).rules(rules).analyze();
        assertThat(result, matchesRulesExactly());
    }
}
