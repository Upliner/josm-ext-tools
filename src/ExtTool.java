import java.util.ArrayList;
import java.util.List;


public class ExtTool {
    
	boolean enabled;
	public String name;

    public String cmdline;

    static ArrayList<ExtTool> tools = new ArrayList<ExtTool>();

    public boolean isEnabled()
    {
    	return enabled;
    }
    public void setEnabled(boolean enabled)
    {
    	this.enabled = enabled;
    }
    public String getName()
    {
        return name;
    }
    public ExtTool()
    {
        this.enabled = true;
    }
    public ExtTool(String name)
    {
        this();
        this.name=name;
    }
    public static List<ExtTool> getToolsList()
    {
        if (tools.size() == 0)
        {
            tools.add(new ExtTool("Fuzzer"));
            tools.add(new ExtTool("Puzzer"));
            tools.add(new ExtTool("Muzzer"));
        }
        return tools;
    }
    public static void addTool(ExtTool tool)
    {
        tools.add(tool);
    }
}
