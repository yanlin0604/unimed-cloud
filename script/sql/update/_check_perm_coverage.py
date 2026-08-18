"""后端各层 @SaCheckPermission 权限码 与 线上 sys_menu 承载情况核对"""
import glob
import io
import os
import re
from collections import defaultdict

import pymysql

layers = defaultdict(set)
root = 'unimed-chronic/unimed-chronic-biz/src/main/java/org/dromara/chronic/controller'
for f in glob.glob(root + '/*/*.java'):
    layer = os.path.basename(os.path.dirname(f))
    layers[layer] |= set(re.findall(r'@SaCheckPermission\("([^"]+)"\)', io.open(f, encoding='utf-8').read()))

conn = pymysql.connect(host='47.113.122.118', port=3306, user='root', password='Hao8090#123',
                       database='unimed-cloud', charset='utf8mb4')
cur = conn.cursor()
cur.execute("SELECT perms FROM sys_menu WHERE perms IS NOT NULL AND perms <> ''")
menu = {r[0] for r in cur.fetchall()}
conn.close()

total_missing = 0
for layer in sorted(layers):
    ps = layers[layer]
    missing = sorted(ps - menu)
    print('%-9s 权限码 %3d  菜单已覆盖 %3d  未覆盖 %d' % (layer, len(ps), len(ps & menu), len(missing)))
    for m in missing:
        print('      -', m)
    total_missing += len(missing)
print('\n未覆盖合计: %d' % total_missing)
