package com.zalude.spac.fusion

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
@WebIntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = [SpacFusionApplication.class] )
class RepositoryIntegrationTestBase extends Specification {

}
