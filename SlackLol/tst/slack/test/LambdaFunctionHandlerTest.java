package slack.test;

import java.io.IOException;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import my.slack.base.LambdaFunctionHandler;
import my.slack.base.SlashCommandRequest;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static SlashCommandRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
    	final String command = "/lolsinfo";
    	final String summonerName = "長門有希";
    	final String token = "g9dfInOMQ1G4Js6be0vpwuft";
    	// codec
    	final URLCodec codec = new URLCodec("UTF-8");
        // TODO: set up your sample input object here.
        input = new SlashCommandRequest();
        input.setCommand(command);
        input.setToken(token);
        try {
			input.setText(codec.encode(summonerName));
		} catch (EncoderException e) {
			e.printStackTrace();
		}
    }

    /**
     * Contextを作成<BR>
     * @return
     */
    private Context createContext() {
        TestContext ctx = new TestContext();
        // TODO: customize your context here if needed.
        ctx.setFunctionName("Slack");
        return ctx;
    }

    /** テストを実施<BR> */
    @Test
    public void testLambdaFunctionHandler() {
        LambdaFunctionHandler handler = new LambdaFunctionHandler();
        Context ctx = createContext();
        Object output = handler.handleRequest(input, ctx);
        // TODO: validate output here if needed.
        if (output != null) {
            System.out.println(output.toString());
        }
    }
}
