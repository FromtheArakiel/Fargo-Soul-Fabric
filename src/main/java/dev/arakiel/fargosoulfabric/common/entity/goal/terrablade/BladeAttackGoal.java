package dev.arakiel.fargosoulfabric.common.entity.goal.terrablade;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.summon.PlannedPath;
import dev.arakiel.fargosoulfabric.common.summon.SummonGoal;
import dev.arakiel.fargosoulfabric.utils.EasingCurve;
import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BladeAttackGoal extends SummonGoal<TerraBlade> {

    private boolean firstStrike = true;
    private boolean lastEllipse = false;
    private boolean flipRoll = false;
    private Vec3 lastTargetPos = Vec3.ZERO;

    public BladeAttackGoal(TerraBlade minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.isTarget(minion.getTarget()) && minion.attacking;
    }

    @Override
    public void start() {
        firstStrike = true;
        lastTargetPos = minion.getTarget().getBoundingBox().getCenter();
    }

    @Override
    public void tick() {
        PlannedPath currentPath = minion.getCurrentPath();
        if (currentPath != null && currentPath.getCurrentIndex() == (currentPath.getNodes().size() / 3)) {
            minion.hitTargets.clear();
        }
        applyPositionCorrection();
        if (minion.isTargetChange()) {
            planChainStrike();
            lastEllipse = false;
        }
        if (!minion.isExecutingPath()) {
            if (firstStrike) {
                planFirstStrike();
                firstStrike = false;
                lastEllipse = false;
            } else {
                if (minion.getOwner().getRandom().nextDouble() < 0.75 && !flipRoll) {
                    planEllipseSlash();
                    lastEllipse = true;
                } else {
                    planHourglassSlash();
                    lastEllipse = false;
                    flipRoll = false;
                }
            }
        }
    }

    private void planFirstStrike() {
        LivingEntity target = minion.getTarget();
        Vec3 start = minion.getPos();
        Vec3 end = target.getBoundingBox().getCenter();
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1e-4) {
            direction = new Vec3(0, 0, 1);
        }
        end = end.add(direction.normalize().scale(2));
        Vec3 planeNormal = direction.cross(new Vec3(0, 1, 0)).normalize();
        if (planeNormal.lengthSqr() < 1e-4) {
            planeNormal = new Vec3(1, 0, 0);
        }
        List<PathNode> nodes = new ArrayList<>();
        int duration = 7;
        for (int i = 1; i <= duration; i++) {
            float progress = (float) i / duration;
            nodes.add(minion.getEulerNode(start.lerp(end, EasingCurve.EASE_IN_OUT_QUAD.apply(progress)), direction, planeNormal));
        }
        minion.setPath(nodes);
    }

    private void planEllipseSlash() {
        // 目标头顶4格、半径4格的圆上，选择离startPos最近的位置
        Vec3 center = lastTargetPos.add(0, 3, 0);
        Vec3 startPos = minion.getPos();
        Vec3 toStart = startPos.subtract(center);
        double angle = Math.atan2(toStart.z, toStart.x);
        Vec3 attackPrepPos = center.add(Math.cos(angle) * 5, 0, Math.sin(angle) * 5);

        // 获取当前运动状态
        Vec3 currentVel = minion.getCurrentVelocity();

        Vec3 planeNormal = lastEllipse ? minion.getCurrentNormal() : Ellipse.randomPlaneNormal(minion.getOwner().getRandom(), lastTargetPos, attackPrepPos);

        Ellipse ellipse = new Ellipse(lastTargetPos, attackPrepPos, planeNormal, 0.45f);

        PathNode attackPrepPathNode = minion.getEulerNode(ellipse.getPoint(0), ellipse.getPoint(0)
                .subtract(ellipse.getCenter()).normalize(), planeNormal);

        List<PathNode> nodes = new ArrayList<>();

        int duration = 14;
        int prepTicks = Math.min(4, (int) (startPos.distanceTo(attackPrepPos)));
        if (prepTicks > 0) {
            for (int i = 0; i <= prepTicks; i++) {
                float progress = (float) i / prepTicks;
                Vec3 point = minion.calculateBezierPoint(progress, startPos, startPos.add(currentVel), attackPrepPos);
                PathNode lerp = minion.getCurrentPathNode().lerp(attackPrepPathNode, progress);
                nodes.add(new PathNode(point, lerp.yaw(), lerp.pitch(), lerp.roll()));
            }
        }
        int attackTicks = duration - prepTicks;
        int halfTicks = attackTicks / 2;

        for (int i = 0; i < halfTicks; i++) {
            float progress = (float) i / attackTicks;
            Vec3 point = ellipse.getPoint(progress);
            Vec3 tipDir = point.subtract(ellipse.getCenter()).normalize();
            nodes.add(minion.getEulerNode(point, tipDir, planeNormal));
        }

        flipRoll = minion.getOwner().getRandom().nextDouble() < 0.25;
        Ellipse flipEllipse = null;
        if (flipRoll) {
            Vec3 mirrorPos = lastTargetPos.add(lastTargetPos.x - attackPrepPos.x, attackPrepPos.y - lastTargetPos.y, lastTargetPos.z - attackPrepPos.z);
            flipEllipse = new Ellipse(lastTargetPos, mirrorPos, planeNormal.scale(-1), 0.6f);
        }

        for (int i = halfTicks; i < attackTicks; i++) {
            float progress = (float) i / attackTicks;
            if (flipRoll && flipEllipse != null) {
                Vec3 point = flipEllipse.getPoint(progress);
                Vec3 tipDir = flipEllipse.getCenter().subtract(point).normalize();
                nodes.add(minion.getEulerNode(point, tipDir, planeNormal));
            } else {
                Vec3 point = ellipse.getPoint(progress);
                Vec3 tipDir = point.subtract(ellipse.getCenter()).normalize();
                nodes.add(minion.getEulerNode(point, tipDir, planeNormal));
            }
        }
        minion.setPath(nodes);
    }

    private void planHourglassSlash() {
        LivingEntity target = minion.getTarget();
        Vec3 startPos = minion.getPos();
        // 目标头顶4格、半径4格的圆上，选择离startPos最近的位置
        Vec3 center = new Vec3(target.getX(), target.getY() + 3, target.getZ());
        Vec3 toStart = startPos.subtract(center);
        double angle = Math.atan2(toStart.z, toStart.x);
        Vec3 attackPrepPos = center.add(Math.cos(angle) * 5, 0, Math.sin(angle) * 5);
        Vec3 endPos = target.getBoundingBox().getCenter();

        // 计算攻击方向
        Vec3 attackDir = endPos.subtract(attackPrepPos);
        if (attackDir.lengthSqr() < 1e-5) {
            attackDir = new Vec3(0, -1, 0);
        }
        endPos = endPos.add(attackDir.normalize().scale(3));

        // 获取当前运动状态
        Vec3 currentVel = minion.getCurrentVelocity();
        Vec3 currentTip = Vec3.directionFromRotation(minion.getPitch(), minion.getYaw()).normalize();
        Vec3 currentNormal = minion.getCurrentNormal();

        List<PathNode> nodes = new ArrayList<>();

        PathNode attackStartNode = null;
        int prepTicks = 7;
        for (int i = 0; i <= prepTicks; i++) {
            float progress = EasingCurve.EASE_OUT_QUAD.apply((float) i / prepTicks);
            Vec3 point = minion.calculateBezierPoint(progress, startPos, startPos.add(currentVel), attackPrepPos);
            Vec3 tipDir = currentTip.lerp(attackDir, progress);
            PathNode pathNode = minion.getEulerNode(point, tipDir, currentNormal);
            nodes.add(pathNode);
            attackStartNode = pathNode;
        }

        PathNode attackEndNode = new PathNode(endPos, attackStartNode.yaw(), attackStartNode.pitch(), attackStartNode.roll());
        int attackTicks = 5;
        for (int i = 0; i <= attackTicks; i++) {
            float progress = EasingCurve.EASE_IN_QUAD.apply((float) i / attackTicks);
            nodes.add(attackStartNode.lerp(attackEndNode, progress));
        }
        minion.setPath(nodes);
    }

    private void planChainStrike() {
        Vec3 endPos = minion.getTarget().getBoundingBox().getCenter();
        int duration = 7;
        Vec3 currentNormal = minion.getCurrentNormal();
        List<PathNode> nodes = new ArrayList<>();
        Ellipse ellipse = new Ellipse(endPos, minion.getPos(), currentNormal, 0.25f);
        for (int i = 1; i <= duration; i++) {
            float progress = (float) i / duration;
            Vec3 ellipseP = ellipse.getPoint(progress * 0.5f);
            Vec3 tipDir = ellipseP.subtract(ellipse.getCenter()).normalize();
            nodes.add(minion.getEulerNode(ellipseP, tipDir, currentNormal));
        }
        minion.setPath(nodes);
    }

    private void applyPositionCorrection() {
        LivingEntity target = minion.getTarget();
        Vec3 currentTargetCenter = target.getBoundingBox().getCenter();
        Vec3 offset = currentTargetCenter.subtract(lastTargetPos);
        if (offset.lengthSqr() > 1e-5) {
            PlannedPath path = minion.getCurrentPath();
            if (path != null) {
                List<PathNode> nodes = path.getNodes();
                int startIdx = path.getCurrentIndex();
                int remaining = nodes.size() - startIdx;
                for (int i = 0; i < remaining; i++) {
                    PathNode node = nodes.get(startIdx + i);
                    float weight = (float) (i + 1) / remaining;
                    Vec3 blendedOffset = offset.scale(weight);
                    nodes.set(startIdx + i, new PathNode(node.pos().add(blendedOffset), node.yaw(), node.pitch(), node.roll()));
                }
            }
        }
        lastTargetPos = currentTargetCenter;
    }

public static class Ellipse {
   private final Vec3 pointA;
   private final Vec3 pointB;
   private final Vec3 planeNormal;
   private final float curvature;
   private final Vec3 center;
   private final Vec3 major;
   private final Vec3 minor;
   private final Vec3 majorDir;
   private final Vec3 minorDir;

   public Ellipse(Vec3 pointA, Vec3 pointB, Vec3 planeNormal, float curvature) {
      this.pointA = pointA;
      this.pointB = pointB;
      this.planeNormal = planeNormal;
      this.curvature = curvature;
      this.major = pointB.subtract(pointA).scale(0.5);
      this.center = pointA.add(this.major);
      this.majorDir = this.major.normalize();
      Vec3 mDir = planeNormal.cross(this.majorDir).normalize();
      if (mDir.lengthSqr() < 1.0E-5) {
         mDir = this.majorDir.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      }

      this.minorDir = mDir;
      this.minor = this.minorDir.scale(this.major.length() * (double)curvature);
   }

   @NotNull
   public static Vec3 randomPlaneNormal(RandomSource random, Vec3 pointA, Vec3 pointB) {
      Vec3 majorDir = pointB.subtract(pointA).normalize();
      float angle = random.nextFloat() * (float) (Math.PI * 2);
      Quaternionf q = new Quaternionf().rotateAxis(angle, (float)majorDir.x, (float)majorDir.y, (float)majorDir.z);
      Vec3 initNormal = majorDir.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      if (initNormal.lengthSqr() < 1.0E-5) {
         initNormal = majorDir.cross(new Vec3(1.0, 0.0, 0.0)).normalize();
      }

      Vector3f rotated = new Vector3f((float)initNormal.x, (float)initNormal.y, (float)initNormal.z).rotate(q);
      return new Vec3((double)rotated.x(), (double)rotated.y(), (double)rotated.z()).normalize();
   }

   public Vec3 getPoint(float progress) {
      float biasedT = progress - 0.08F * Mth.sin(progress * (float) (Math.PI * 2));
      float theta = biasedT * (float) (Math.PI * 2);
      return this.center.add(this.major.scale(Math.cos((double)theta))).add(this.minor.scale(Math.sin((double)theta)));
   }

   public Vec3 getTangent(float progress) {
      float biasedT = progress - 0.08F * Mth.sin(progress * (float) (Math.PI * 2));
      float theta = biasedT * (float) (Math.PI * 2);
      return this.major.scale(-Math.sin((double)theta)).add(this.minor.scale(Math.cos((double)theta))).normalize();
   }

   public Vec3 getCenter() {
      return this.center;
   }

   public Vec3 getMajorDir() {
      return this.majorDir;
   }

   public Vec3 getMinorDir() {
      return this.minorDir;
   }

   public Vec3 getPlaneNormal() {
      return this.planeNormal;
   }

   public float getCurvature() {
      return this.curvature;
   }

   public Vec3 getPointA() {
      return this.pointA;
   }

   public Vec3 getPointB() {
      return this.pointB;
   }

   public Vec3 getFocusNearA() {
      double c = Math.sqrt(Math.max(0.0, this.major.lengthSqr() - this.minor.lengthSqr()));
      return this.center.add(this.majorDir.scale(-c));
   }

   public Vec3 getFocusNearB() {
      double c = Math.sqrt(Math.max(0.0, this.major.lengthSqr() - this.minor.lengthSqr()));
      return this.center.add(this.majorDir.scale(c));
   }
}
}
