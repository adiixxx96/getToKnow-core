package com.adape.gtk.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.springframework.stereotype.Component;

@Component("Utils")
public class Utils {
	
	public static String printStackTraceToLog(Exception e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();
    } 

	public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
	
	public static TreeNode<String> buildTree(List<String> names) {
		return buildTree(names.toArray(new String[names.size()]));
	}
	
	@SuppressWarnings("rawtypes")
	public static TreeNode<String> buildTree(String[] names){
		// tree root
	    TreeNode<String> tree = new TreeNode<String>("root");
	    
	    // for each parameter...
	    for (int i=0;i<names.length;i++){
	        String[] splitName = names[i].split("\\.");
	        TreeNode<String> parent = tree;
	        
	        // loop over the split name and see if the nodes exist in the tree. If not, create them
	        for (int n=0;n<splitName.length;n++){
	        	String name = splitName[n];
	        	
	        	// comparable to use in the find function (check if the name is equals to the object's data)
				Comparable<TreeNode> searchCriteria = new Comparable<TreeNode>() {
	        		@Override
	        		public int compareTo(TreeNode treeData) {
	        			if (treeData == null)
	        				return 1;
	        			boolean nodeOk = treeData.data.equals(name);
	        			return nodeOk ? 0 : 1;
	        		}
	        	};
	        	// the parent node exists, so it doesn't need to be created. Store the node as 'parent' to use in the next loop run
	            if (parent.findTreeNodeObject(searchCriteria) != null){
	            	// allow a parent-child with the same name
	            	if (parent.data.equals(parent.findTreeNodeObject(searchCriteria).data))
	            		parent = parent.addChild(name);
	            	else
	            		parent = parent.findTreeNodeObject(searchCriteria);
	            }
	            else {
	            	// the node doesn't exist, so create it. Then set it as 'parent' for use by the next loop run
	                parent = parent.addChild(name);
	            }
	        }
	    }
	    return tree;
	}
	
	public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (str.isEmpty()){
            return true;
        }
        if (str.isBlank()) {
        	return true;
        }
        
        return false;
    }
}