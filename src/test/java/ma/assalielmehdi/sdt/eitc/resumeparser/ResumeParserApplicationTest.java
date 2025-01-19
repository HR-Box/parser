package ma.assalielmehdi.sdt.eitc.resumeparser;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ResumeParserApplicationTest {
  @Test
  void test() {
    var modules = ApplicationModules.of(ResumeParserApplication.class).verify();

    new Documenter(modules)
      .writeModulesAsPlantUml()
      .writeIndividualModulesAsPlantUml();
  }
}
