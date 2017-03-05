package my.slack.base;

import java.util.List;
import java.util.logging.Level;

import javax.naming.AuthenticationException;

import org.apache.commons.codec.net.URLCodec;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.current_game.dto.CurrentGameInfo;
import net.rithms.riot.api.endpoints.match.dto.MatchDetail;
import net.rithms.riot.api.endpoints.matchlist.dto.MatchList;
import net.rithms.riot.api.endpoints.matchlist.dto.MatchReference;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.PlatformId;
import net.rithms.riot.constant.Region;

public class LambdaFunctionHandler implements RequestHandler<SlashCommandRequest, Object> {
	// logger
	LambdaLogger logger;
	// codec
	final URLCodec codec = new URLCodec("UTF-8");
	/** RIOT API */
	private RiotApi api;

	StringBuffer sb = new StringBuffer("");

	@Override
	public Object handleRequest(SlashCommandRequest input, Context context) {
		logger = context.getLogger();
		init();
		logger.log("Input: " + input);
		SlashCommandResponse dto = new SlashCommandResponse();
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
	private String getSummonerInfo(String summonerName)  {
		logger.log("SummonerName=" + summonerName);
		Summoner summonerInfo = null;
		try {
			// Summoner Info Get
			summonerInfo = api.getSummonerByName(Region.JP, summonerName);
		} catch (RiotApiException e) {
			logger.log(e.getMessage());
		}

		// Summoner Info null check
		if(summonerInfo == null) {
			logger.log("SummonerName is null");
			return "Didnt get summoner info";
		}

		CurrentGameInfo gameInfo = null;
		try {
			// Current Game Info Get
			gameInfo = api.getCurrentGameInfo(PlatformId.JP, summonerInfo.getId());
			addResult("\n\r CurrentGameInfo :  \n");
			addResult(gameInfo.toString(true));
		} catch (RiotApiException e) {
			logger.log(e.getMessage());
		}

//		RecentGames recentGame = null;
//		try {
//			// Recent Game Info Get
//			recentGame = api.getRecentGames(Region.JP, summonerInfo.getId());
//			addResult("\n\r RecentGameInfo :  \n");
//			addResult(recentGame.toString(true));
//		} catch (RiotApiException e) {
//			logger.log(e.getMessage());
//		}

		// Match Info Get
		MatchList match = null;
		try {
			match = api.getMatchList(Region.JP, summonerInfo.getId());
			addResult("\n\r Match Info : \n");
			addResult(match.toString(true));
		} catch (RiotApiException e) {
			logger.log(e.getMessage());
		}

		// Match Detail Info Get
		if(match != null) {
			int count = 0;
			List<MatchReference> matchRef = match.getMatches();
			for(MatchReference ref : matchRef) {
				try {
					if(count > 3) {
						break;
					}
					MatchDetail detail = api.getMatch(Region.JP, ref.getMatchId(), true);
					addResult("\n\r MatchDetail Info : \n");
					addResult(detail.toString(true));
					count++;
				} catch (RiotApiException e) {
					logger.log(e.getMessage());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 結果にエレメントを追加してログを記録します<BR>
	 * @param sb
	 * @param element
	 */
	private void addResult(String element) {
		sb.append(element);
		logger.log(element);
	}
}
