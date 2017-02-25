package my.slack.base;

import java.util.logging.Level;

import javax.naming.AuthenticationException;

import org.apache.commons.codec.net.URLCodec;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.current_game.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.PlatformId;
import net.rithms.riot.constant.Region;

public class LambdaFunctionHandler implements RequestHandler<SlackInputDto, Object> {
	// logger
	LambdaLogger logger;
	// codec
	final URLCodec codec = new URLCodec("UTF-8");
	/** RIOT API */
	private RiotApi api;

	@Override
	public Object handleRequest(SlackInputDto input, Context context) {
		logger = context.getLogger();
		init();
		logger.log("Input: " + input);
		OutputDto dto = new OutputDto();
		// 認証
		try {
			final String command = codec.decode(input.getCommand());
			final String inputText = codec.decode(input.getText());
			String returnText = "empty";
			logger.log("Called Command:" + command);
			logger.log("Called InputText:" + inputText);
			switch (command) {
			case "/lolwinrate":
				break;
			case "/lolsinfo":
				if ("g9dfInOMQ1G4Js6be0vpwuft".equals(input.getToken())) {
					returnText = "【" + getSummonerInfo(inputText) + "】";
					logger.log("ResultText:" + returnText);
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

	/** 初期化します<BR> */
	private void init() {
		ApiConfig config = new ApiConfig().setDebugLevel(Level.FINEST).setKey(APIKey.RIOT_API_KEY);
		api = new RiotApi(config);
	}
	/**
	 * サモナー情報を取得します<BR>
	 * @param summonerName
	 * @return
	 * @throws Exception
	 */
	private String getSummonerInfo(String summonerName) throws Exception {
		Summoner summonerInfo = api.getSummonerByName(Region.JP, summonerName);
		CurrentGameInfo gameInfo = api.getCurrentGameInfo(PlatformId.JP, summonerInfo.getId());
		return gameInfo.toString(true);
	}
}
