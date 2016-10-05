package modeler.parser.tags;

/**
 * 
 * @author olivier
 * List of tags for the xml network entry file
 * The objective of this parser is to build a java representation
 * of the network, starting from an entry xml file
 */
public class XMLNetworkTags {
	/* Config Tags */
	public static final String TAG_CONFIG		= "Config";
	public static final String TAG_NAME			= "name";
	public static final String TAG_P_INPUT		= "portsinput";
	public static final String TAG_P_OUTPUT		= "portsoutput";
	public static final String TAG_TIME_LIMIT	= "time-limit";
	public static final String TAG_CRIT_SWITCHES= "critswitches";
	public static final String TAG_CRIT_SWITCH	= "critswitch";
	public static final String TAG_ELECTRONICAL_LATENCY = "elatency";
	public static final String TAG_AUTO_LOAD	= "autoload";
	public static final String TAG_SPEED_MACHINE= "speed";
	
	/* WCTT Model */
	public static final String TAG_WCTT_COMPUTE = "wcttmodel";
	public static final String TAG_WCTT_RATE 	= "wcttrate";
	public static final String TAG_WORST_CASE_ANALYSIS = "wcanalysis";
	
	/* Tasks automatic generation */
	public static final String TAG_AUTOGEN_TASKS= "autogen";
	public static final String TAG_HIGH_WCTT	= "highestwctt";
	
	/* XML Tag for the number of auto-generated tasks */
	public static final String TAG_AUTO_TASKS	= "autotasks";
	
	
	/* Machine tags */
	public static final String TAG_MACHINE		= "machine"; 
	public static final String TAG_LINKS		= "links"; 
	public static final String TAG_MACHINELINK	= "machinel"; 
	
	/* Message tags */
	public static final String TAG_MESSAGE 		= "message"; 
	public static final String TAG_PRIORITY 	= "priority"; 
	public static final String TAG_CRITICALITY 	= "criticality"; 
	public static final String TAG_PERIOD 		= "period"; 
	public static final String TAG_OFFSET		= "offset"; 
	public static final String TAG_WCET			= "wcet"; 
	public static final String TAG_PATH			= "path";
	
	/* Mixed-criticality */
	public static final String TAG_MC_PROTO 	= "protocol";
	public static final String TAG_MC_MODEL		= "switch";
}
