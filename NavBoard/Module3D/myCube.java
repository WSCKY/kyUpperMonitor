package Module3D;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

public class myCube extends Shape3D {
//	private double centerX = 1.0;
//	private double centerY = 1.0;
//	private double centerZ = 1.0;
//	private float size = 0.5f;

	public myCube() {
		this.setGeometry(createGeometry());
	};

	private Geometry createGeometry() {
		IndexedLineArray axisLines = new IndexedLineArray(4, GeometryArray.COORDINATES, 12);
		axisLines.setCoordinate(0, new Point3d(0, 0, 0));
		axisLines.setCoordinate(1, new Point3d(1, 0, 0));
		axisLines.setCoordinate(2, new Point3d(0, 1, 0));
		axisLines.setCoordinate(3, new Point3d(0, 0, 1));
		axisLines.setCoordinateIndex(0, 0);
		axisLines.setCoordinateIndex(1, 1);
		axisLines.setCoordinateIndex(2, 0);
		axisLines.setCoordinateIndex(3, 2);
		axisLines.setCoordinateIndex(4, 0);
		axisLines.setCoordinateIndex(5, 3);
		axisLines.setCoordinateIndex(6, 1);
		axisLines.setCoordinateIndex(7, 2);
		axisLines.setCoordinateIndex(8, 1);
		axisLines.setCoordinateIndex(9, 3);
		axisLines.setCoordinateIndex(10, 2);
		axisLines.setCoordinateIndex(11, 3);
//		IndexedLineArray axisLines = new IndexedLineArray(8, GeometryArray.COORDINATES, 24);
//
//		axisLines.setCoordinate(0, new Point3d(centerX - size, centerZ + size, centerY - size));
//		axisLines.setCoordinate(1, new Point3d(centerX + size, centerZ + size, centerY - size));
//		axisLines.setCoordinate(2, new Point3d(centerX - size, centerZ + size, centerY + size));
//		axisLines.setCoordinate(3, new Point3d(centerX + size, centerZ + size, centerY + size));
//		axisLines.setCoordinate(4, new Point3d(centerX - size, centerZ - size, centerY + size));
//		axisLines.setCoordinate(5, new Point3d(centerX - size, centerZ - size, centerY - size));
//		axisLines.setCoordinate(6, new Point3d(centerX + size, centerZ - size, centerY - size));
//		axisLines.setCoordinate(7, new Point3d(centerX + size, centerZ - size, centerY + size));
//
//		axisLines.setCoordinateIndex(0, 0);
//		axisLines.setCoordinateIndex(1, 1);
//		axisLines.setCoordinateIndex(2, 0);
//		axisLines.setCoordinateIndex(3, 2);
//		axisLines.setCoordinateIndex(4, 2);
//		axisLines.setCoordinateIndex(5, 3);
//		axisLines.setCoordinateIndex(6, 3);
//		axisLines.setCoordinateIndex(7, 1);
//		axisLines.setCoordinateIndex(8, 0);
//		axisLines.setCoordinateIndex(9, 5);
//		axisLines.setCoordinateIndex(10, 5);
//		axisLines.setCoordinateIndex(11, 6);
//		axisLines.setCoordinateIndex(12, 6);
//		axisLines.setCoordinateIndex(13, 7);
//		axisLines.setCoordinateIndex(14, 7);
//		axisLines.setCoordinateIndex(15, 4);
//		axisLines.setCoordinateIndex(16, 4);
//		axisLines.setCoordinateIndex(17, 2);
//		axisLines.setCoordinateIndex(18, 5);
//		axisLines.setCoordinateIndex(19, 4);
//		axisLines.setCoordinateIndex(20, 7);
//		axisLines.setCoordinateIndex(21, 3);
//		axisLines.setCoordinateIndex(22, 6);
//		axisLines.setCoordinateIndex(23, 1);

		return axisLines;
	}
}
