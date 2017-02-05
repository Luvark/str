package slack.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import slack.riot.RiotApiKey;

public class LambdaFunctionHandler implements RequestHandler<SlackInputDto, Object> {
	String accessKey = "zviT1HC8FMAENppN25JdMvgi";

	@Override
	public Object handleRequest(SlackInputDto input, Context context) {
		context.getLogger().log("Input: " + input);
		int count = 3;

		LambdaLogger lambdaLogger = context.getLogger();
		lambdaLogger.log("count = " + count);
		OutputDto dto = new OutputDto();
		if (accessKey.equals(input.getToken())) {
			try {
				String inputText = input.getText();
				String[] args = inputText.split("+");
				String returnText = "";
				for(String arg : args) {
					if(arg.startsWith("%40lol")) {
					} else {
						returnText += "【" + getSummonerInfo(arg) + "】";
					}
				}
				dto.setText(returnText);
			} catch (Exception e) {
				dto.setText("例外エラー発生: trigger_word=" + input.getText());
			}
		} else {
			dto.setText("認証エラー");
		}
		return dto;
	}

	private String getSummonerInfo(String summonerName) throws Exception {
		String riotApiUrl = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
		String userName = summonerName;
		String apiKey = RiotApiKey.RIOT_API_KEY;
		String stringUrl = riotApiUrl + userName + "?api_key=" + apiKey;

		System.out.println("userName=" + userName);
		System.out.println("stringUrl=" + stringUrl);

		URL url = new URL(stringUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		System.out.println("reader=" + reader);

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		System.out.println("sb=" + sb);
		return sb.toString();
	}
}
