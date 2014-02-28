package org.jenkinsci.test.acceptance.po;

import com.google.inject.Injector;
import org.jenkinsci.test.acceptance.cucumber.By2;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Job extends PageObject {
    public final String name;

    public Job(Injector injector, URL url, String name) {
        super(injector,url);
        this.name = name;
    }

    public <T extends BuildStep> T addBuildStep(Class<T> type) throws Exception {
        ensureConfigPage();

        String caption = type.getAnnotation(BuildStepPageObject.class).value();

        selectStep(caption, find(By2.path("/hetero-list-add[builder]")));
        List<WebElement> all = all(By2.xpath("//div[@name='builder']"));

        String path = all.get(all.size()-1).getAttribute("path");

        return type.getConstructor(Job.class,String.class).newInstance(this,path);
    }

    public ShellBuildStep addShellStep(String shell) throws Exception {
        ShellBuildStep step = addBuildStep(ShellBuildStep.class);
        step.setCommand(shell);
        return step;
    }

    public URL getBuildUrl() throws Exception {
        return new URL(url,"build?delay=0sec");
    }

    public Build queueBuild(Parameter... parameters) throws Exception {
        int nb = getJson().get("nextBuildNumber").intValue();
        visit(getBuildUrl());

        return build(nb).waitUntilStarted();
    }

    public Build build(int buildNumber) throws Exception {
        return new Build(this,buildNumber);
    }

    public Build getLastBuild() throws Exception {
        return new Build(this,"lastBuild");
    }
}
