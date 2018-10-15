//
//public class DecTreeNodeImpl extends DecTreeNode{
//	//If leaf, label to return.
//		int classLabel;
//		//Attribute split label.
//		String attribute;
//		//Threshold that attributes are split on.
//		public double threshold;
//		//Left child. Can directly access and update. <= threshold.
//		public DecTreeNodeImpl left = null;
//		//Right child. Can directly access and update. > threshold.
//		public DecTreeNodeImpl right = null;
//		DecTreeNodeImpl(int classLabel, String attribute, double threshold) {
//			if(classLabel == -1){
//				return;
//			}else{
//				this.classLabel = classLabel;
//			}
//			this.attribute = attribute;
//			this.threshold = threshold;
//		
//		}
//		public boolean isLeaf(){
//			return this.left == null && this.right == null;
//		}
//	}
