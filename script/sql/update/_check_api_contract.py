"""三端前端 API 调用点与后端控制器路由一致性校验

只解析真实调用点（http.xxx(...) / alovaInstance.xxx(...)）；
Api 枚举按文件作用域解析（各模块都有 Api.root，全局解析会串味）。
"""
import io
import os
import re
import glob
from collections import defaultdict

CTRL_ROOT = 'unimed-chronic/unimed-chronic-biz/src/main/java/org/dromara/chronic/controller'
HTTP_CALL = r'http\.(?:get|post|put|delete|upload)\s*(?:<[^>]*>)?\s*\('
ALOVA_CALL = (r'alovaInstance\.(?:get|post|put|delete|postWithMsg|putWithMsg|deleteWithMsg'
              r'|postForm|download|upload)\s*(?:<[^>]*>)?\s*\(')
FRONTS = {
    'chronic-doctor': ('web/chronic-doctor/src', HTTP_CALL),
    'chronic-patient': ('web/chronic-patient/src', HTTP_CALL),
    'chronic-admin': ('web/chronic-admin-vue/apps/web-antd/src', ALOVA_CALL),
}
ENUM_RE = r"(\w+)\s*=\s*'(/chronic/[^']*)'"


def strip_expr(u):
    """按花括号配对剥离 ${...} 模板表达式（支持嵌套，如 ${buildQuery({a, b})}）"""
    out, i = '', 0
    while i < len(u):
        if u.startswith('${', i):
            depth, j = 0, i + 1
            while j < len(u):
                if u[j] == '{':
                    depth += 1
                elif u[j] == '}':
                    depth -= 1
                    if depth == 0:
                        break
                j += 1
            out += '{}'
            i = j + 1
        else:
            out += u[i]
            i += 1
    return out


def norm(u):
    u = strip_expr(u).split('?')[0]
    u = re.sub(r'\{[^}]*\}', '{}', u)
    u = re.sub(r'/+', '/', u).rstrip('/')
    return u


def backend_routes():
    routes = set()
    for f in glob.glob(CTRL_ROOT + '/*/*.java'):
        src = io.open(f, encoding='utf-8').read()
        head = src.split('public class')[0] if 'public class' in src else src
        pm = re.search(r'@RequestMapping\(\s*(?:value\s*=\s*)?"([^"]+)"\s*\)', head)
        prefix = pm.group(1) if pm else ''
        for mm in re.finditer(r'@(?:Get|Post|Put|Delete|Patch)Mapping\(\s*(?:value\s*=\s*)?"([^"]*)"', src):
            routes.add(norm(prefix + mm.group(1)))
        for _ in re.finditer(r'@(?:Get|Post|Put|Delete|Patch)Mapping(?:\s*\(\s*\))?\s*(?:\r?\n)', src):
            if prefix:
                routes.add(norm(prefix))
    return routes


def collect(root, call_re):
    files = glob.glob(root + '/**/*.ts', recursive=True) + glob.glob(root + '/**/*.vue', recursive=True)
    calls = defaultdict(set)
    for f in files:
        src = io.open(f, encoding='utf-8').read()
        rel = f.replace(root + os.sep, '').replace('\\', '/')
        enums = dict(re.findall(ENUM_RE, src))
        for m in re.finditer(call_re, src):
            seg = src[m.end(): m.end() + 400]
            um = re.match(r"\s*[`'\"]([^`'\"]+)[`'\"]", seg)
            if not um:
                continue
            url = re.sub(r'\$\{Api\.(\w+)\}', lambda x: enums.get(x.group(1), '${UNRESOLVED}'), um.group(1))
            if url.startswith('/chronic/'):
                calls[url].add(rel)
    return calls


def main():
    backend = backend_routes()
    print('后端路由数: %d' % len(backend))
    total_bad = 0
    for name, (root, call_re) in FRONTS.items():
        calls = collect(root, call_re)
        bad, unknown = [], []
        for u, files in sorted(calls.items()):
            cand = norm(u)
            alts = {cand}
            # 末尾占位符可能是 ${buildQuery(...)} 这类 query 拼接，去掉再比一次
            if cand.endswith('/{}'):
                alts.add(cand[:-3])
            if cand.endswith('{}'):
                alts.add(cand[:-2].rstrip('/'))
            for a in list(alts):
                if a.startswith('/chronic/chronic/'):
                    alts.add(a[len('/chronic'):])
            if u.count('(') != u.count(')'):
                unknown.append((u, sorted(files)))
                continue
            if not (alts & backend):
                bad.append((u, sorted(files)))
        print('\n=== %s ===  调用点 URL %d  打不通 %d' % (name, len(calls), len(bad)))
        for u, files in bad:
            print('   X %-70s  %s' % (u[:70], ', '.join(files[:2])))
        for u, files in unknown:
            print('   ? %-70s  %s (多行模板，需人工确认)' % (u[:70], ', '.join(files[:2])))
        total_bad += len(bad)
    print('\n总计打不通的调用点: %d' % total_bad)


main()
