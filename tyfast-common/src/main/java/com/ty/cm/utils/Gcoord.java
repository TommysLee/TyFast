package com.ty.cm.utils;

/**
 * Gcoord Java 版本
 * 坐标系转换工具，支持：WGS84, GCJ02, BD09, BD09MC, Web Mercator
 * 完全参考 https://github.com/hujiulong/gcoord 实现
 */
public final class Gcoord {

    // ---------- 常量 ----------
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double PI = Math.PI;
    private static final double HALF_PI = PI / 2;
    private static final double PI_2 = PI * 2;

    // ---------- 枚举：坐标系类型 ----------
    public enum CoordType {
        WGS84,
        GCJ02,
        BD09,
        BD09MC,
        WEB_MERCATOR
    }

    // ---------- 核心转换方法 ----------
    public static double[] transform(double[] coord, CoordType from, CoordType to) {
        if (coord == null || coord.length < 2) {
            throw new IllegalArgumentException("坐标数组至少包含经度和纬度");
        }
        if (from == to) {
            return coord.clone();
        }

        double lon = coord[0];
        double lat = coord[1];

        // 直接转换对（无误差积累）
        if (from == CoordType.WGS84 && to == CoordType.GCJ02) {
            return wgs84ToGcj02(lon, lat);
        }
        if (from == CoordType.GCJ02 && to == CoordType.WGS84) {
            return gcj02ToWgs84(lon, lat);
        }
        if (from == CoordType.GCJ02 && to == CoordType.BD09) {
            return gcj02ToBd09(lon, lat);
        }
        if (from == CoordType.BD09 && to == CoordType.GCJ02) {
            return bd09ToGcj02(lon, lat);
        }
        if (from == CoordType.WGS84 && to == CoordType.WEB_MERCATOR) {
            return wgs84ToWebMercator(lon, lat);
        }
        if (from == CoordType.WEB_MERCATOR && to == CoordType.WGS84) {
            return webMercatorToWgs84(lon, lat);
        }
        if (from == CoordType.BD09MC && to == CoordType.BD09) {
            return bd09mcToBd09(lon, lat);
        }
        if (from == CoordType.BD09 && to == CoordType.BD09MC) {
            return bd09ToBd09mc(lon, lat);
        }

        // 间接转换：先转成 WGS84，再转到目标
        double[] wgs84 = toWgs84(lon, lat, from);
        return fromWgs84(wgs84[0], wgs84[1], to);
    }

    // ---------- 辅助：任意坐标系 -> WGS84 ----------
    private static double[] toWgs84(double lon, double lat, CoordType from) {
        switch (from) {
            case WGS84:
                return new double[]{lon, lat};
            case GCJ02:
                return gcj02ToWgs84(lon, lat);
            case BD09: {
                double[] gcj = bd09ToGcj02(lon, lat);
                return gcj02ToWgs84(gcj[0], gcj[1]);
            }
            case BD09MC: {
                double[] bd09 = bd09mcToBd09(lon, lat);
                double[] gcj = bd09ToGcj02(bd09[0], bd09[1]);
                return gcj02ToWgs84(gcj[0], gcj[1]);
            }
            case WEB_MERCATOR:
                return webMercatorToWgs84(lon, lat);
            default:
                throw new IllegalArgumentException("不支持的坐标系: " + from);
        }
    }

    // ---------- 辅助：WGS84 -> 任意坐标系 ----------
    private static double[] fromWgs84(double lon, double lat, CoordType to) {
        switch (to) {
            case WGS84:
                return new double[]{lon, lat};
            case GCJ02:
                return wgs84ToGcj02(lon, lat);
            case BD09: {
                double[] gcj = wgs84ToGcj02(lon, lat);
                return gcj02ToBd09(gcj[0], gcj[1]);
            }
            case BD09MC: {
                double[] gcj = wgs84ToGcj02(lon, lat);
                double[] bd09 = gcj02ToBd09(gcj[0], gcj[1]);
                return bd09ToBd09mc(bd09[0], bd09[1]);
            }
            case WEB_MERCATOR:
                return wgs84ToWebMercator(lon, lat);
            default:
                throw new IllegalArgumentException("不支持的坐标系: " + to);
        }
    }

    // ---------- 具体转换算法 ----------
    // 1. WGS84 <-> GCJ02
    private static boolean outOfChina(double lon, double lat) {
        if (lon < 72.004 || lon > 137.8347) return true;
        if (lat < 0.8293 || lat > 55.8271) return true;
        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320.0 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    public static double[] wgs84ToGcj02(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return new double[]{lon, lat};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        return new double[]{lon + dLon, lat + dLat};
    }

    public static double[] gcj02ToWgs84(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return new double[]{lon, lat};
        }
        double wgsLon = lon, wgsLat = lat;
        for (int i = 0; i < 30; i++) {
            double[] gcj = wgs84ToGcj02(wgsLon, wgsLat);
            double dLon = gcj[0] - lon;
            double dLat = gcj[1] - lat;
            if (Math.abs(dLon) < 1e-9 && Math.abs(dLat) < 1e-9) break;
            wgsLon -= dLon;
            wgsLat -= dLat;
        }
        return new double[]{wgsLon, wgsLat};
    }

    // 2. GCJ02 <-> BD09
    public static double[] gcj02ToBd09(double lon, double lat) {
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI * 3000.0 / 180.0);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI * 3000.0 / 180.0);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new double[]{bdLon, bdLat};
    }

    public static double[] bd09ToGcj02(double lon, double lat) {
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI * 3000.0 / 180.0);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI * 3000.0 / 180.0);
        double gcjLon = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        return new double[]{gcjLon, gcjLat};
    }

    // 3. BD09MC <-> BD09
    public static double[] bd09mcToBd09(double lon, double lat) {
        double x = lon / 0.0000001;
        double y = lat / 0.0000001;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI * 3000.0 / 180.0);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI * 3000.0 / 180.0);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new double[]{bdLon, bdLat};
    }

    public static double[] bd09ToBd09mc(double lon, double lat) {
        double x = lon - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI * 3000.0 / 180.0);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI * 3000.0 / 180.0);
        double bdLon = z * Math.cos(theta) * 0.0000001;
        double bdLat = z * Math.sin(theta) * 0.0000001;
        return new double[]{bdLon, bdLat};
    }

    // 4. WGS84 <-> Web Mercator (EPSG:3857)
    public static double[] wgs84ToWebMercator(double lon, double lat) {
        double x = lon * 20037508.34 / 180.0;
        double y = Math.log(Math.tan((90.0 + lat) * PI / 360.0)) / (PI / 180.0);
        y = y * 20037508.34 / 180.0;
        return new double[]{x, y};
    }

    public static double[] webMercatorToWgs84(double x, double y) {
        double lon = x / 20037508.34 * 180.0;
        double lat = y / 20037508.34 * 180.0;
        lat = 180.0 / PI * (2.0 * Math.atan(Math.exp(lat * PI / 180.0)) - HALF_PI);
        return new double[]{lon, lat};
    }
}
