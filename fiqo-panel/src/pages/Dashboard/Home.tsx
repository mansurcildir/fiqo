import { useEffect, useState } from 'react';
import { fileAPI } from '../../service/file-service';
import { FileInfo } from '../../model/file/FileInfo';
import { FileManager } from '@cubone/react-file-manager';
import '@cubone/react-file-manager/dist/style.css';
import ComponentCard from '../../components/common/ComponentCard';
import { UserInfo } from '../../model/user/UserInfo';
import { userAPI } from '../../service/user-service';

interface File {
  name: string;
  isDirectory: boolean;
  path: string;
  size: number;
  updatedAt: string;
}

export default function Home() {
  const [userInfo, setUserInfo] = useState<UserInfo>();
  const [files, setDirectories] = useState<File[]>([]);
  const [currentPath] = useState<string>('');

  // fetch files when path changes
  useEffect(() => {
    const fetchFiles = async () => {
      fileAPI.getAllFiles(currentPath).then((res) => {
        seperateDirs(res.data);
      });
    };

    fetchFiles();
  }, [currentPath]);

  useEffect(() => {
    const getProfile = async () => {
      userAPI.getProfile().then((res) => {
        setUserInfo(res.data);
      });
    };

    getProfile();
  }, []);

  const fetchFiles = async () => {
    fileAPI.getAllFiles(currentPath).then((res) => {
      seperateDirs(res.data);
    });
  };

  const seperateDirs = (files: FileInfo[]) => {
    const dirs: File[] = [];

    files.forEach((file) => {
      const parts = file.path.split('/');
      parts.shift();

      parts.forEach((part, index) => {
        const path = parts.slice(0, index + 1).join('/');

        if (!dirs.find((d) => d.path === path)) {
          dirs.push({
            name: part,
            path: path,
            isDirectory: index + 1 === parts.length ? false : true,
            size: index + 1 === parts.length ? file.size : 0,
            updatedAt: file.updatedAt
          });
        }
      });
    });

    setDirectories(dirs);
  };

  const deleteFile = (path: string, recursive: boolean) => {
    fileAPI.deleteFile(path, recursive).then(() => {
      fetchFiles();
    });
  };

  const downloadFile = (path: string, fileName: string) => {
    fileAPI.downloadFile(path).then((blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      URL.revokeObjectURL(url);
      fetchFiles();
    });
  };

  const CustomPreview: React.FC<{ file: File }> = ({ file }) => {
    const [url, setUrl] = useState<string>();

    useEffect(() => {
      const fetchPreview = async () => {
        fileAPI.downloadFile(`${userInfo?.uuid}/${file.path}`).then((blob) => {
          setUrl(URL.createObjectURL(blob));
        });
      };
      fetchPreview();
    }, [file]);

    if (!url) return <>Loading...</>;
    return <img className="w-full" src={url} alt={file.name} />;
  };

  return (
    <ComponentCard title="File Explorer">
      <FileManager
        primaryColor={'#465fff'}
        files={files}
        filePreviewComponent={(file: File) => <CustomPreview file={file} />}
        onDownload={(files: Array<File>) => {
          files.forEach((file) => {
            downloadFile(userInfo?.uuid + '/' + file.path, file.name);
          });
        }}
        onDelete={(files: Array<File>) => {
          files.forEach((file) => {
            deleteFile(file.path, false);
          });
        }}
      />
    </ComponentCard>
  );
}
