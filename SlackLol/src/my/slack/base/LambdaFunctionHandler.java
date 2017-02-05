package my.slack.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.naming.AuthenticationException;

import org.apache.commons.codec.net.URLCodec;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<SlackInputDto, Object> {
	// RIOT API KEY
	final String apiKey = "YourApikey";
	// logger
	LambdaLogger logger;

	@Override
	public Object handleRequest(SlackInputDto input, Context context) {
		logger = context.getLogger();
		logger.log("Input: " + input);
		OutputDto dto = new OutputDto();
		// 認証
		try {
			final URLCodec codec = new URLCodec("UTF-8");
			final String command = codec.decode(input.getCommand());
			final String inputText = input.getText();
			String returnText = "";
			logger.log(command);
			logger.log(inputText);
			switch (command) {
			case "/lolwinrate":
				break;
			case "/lolsinfo":
				if ("g9dfInOMQ1G4Js6be0vpwuft".equals(input.getToken())) {
					returnText = "【" + getSummonerInfo(inputText) + "】";
					logger.log(returnText);
				} else {
					throw new AuthenticationException();
				}
				break;
			default:
				throw new IllegalArgumentException("存在しないコマンドです" + command);
			}
			dto.setText(returnText);
		} catch (Exception e) {
			dto.setText("例外エラー発生:=" + e.getMessage());
		}
		return dto;
	}

	/**
	 * サモナー情報を取得します<BR>
	 * @param summonerName
	 * @return
	 * @throws Exception
	 */
	private String getSummonerInfo(String summonerName) throws Exception {
		String riotApiUrl = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
		String userName = summonerName;
		String stringUrl = riotApiUrl + userName + "?api_key=" + apiKey;

		logger.log("userName=" + userName);
		logger.log("stringUrl=" + stringUrl);

		URL url = new URL(stringUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		logger.log("reader=" + reader);

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		logger.log("sb=" + sb);
		return sb.toString();
	}
}
