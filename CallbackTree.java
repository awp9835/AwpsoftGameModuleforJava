package awpsoft.gamemodule;

public abstract class CallbackTree
{
	public static void enterCallbackTree(CallbackTree root) {enterCallbackTree(root, null);}
	public static void enterCallbackTree(CallbackTree root, Object params)
	{
		CallbackTree next = root;
		while(next != null) {next = next.run(params);}
	}
	public static Thread asyncEnterCallbackTree(CallbackTree root) {return new AsyncRunner(root).startAndReturnThread();}
	public static Thread asyncEnterCallbackTree(CallbackTree root, Object params) {return new AsyncRunner(root, params).startAndReturnThread();}

	public final void enter() {enterCallbackTree(this, null);}
	public final void enter(Object params) {enterCallbackTree(this, params);}
	public final Thread asyncEnter() {return new AsyncRunner(this).startAndReturnThread();}
	public final Thread asyncEnter(Object params) {return new AsyncRunner(this, params).startAndReturnThread();}

	public abstract CallbackTree run(Object params);

	public static final class AsyncRunner extends Thread
	{
		private CallbackTree Root;
		private Object Params;
		public AsyncRunner(CallbackTree root)
		{ 
			Root = root;
			Params = null; 
		}
		public AsyncRunner(CallbackTree root, Object params)
		{
			Root = root;
			Params = params; 
		}
		@Override public void run() {enterCallbackTree(Root, Params);}
		public Thread startAndReturnThread()
		{
			start();
			return this;
		}
	}
};